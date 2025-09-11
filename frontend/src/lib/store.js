import {get, writable} from "svelte/store";

const API_BASE = "http://localhost:8080/api";

async function fetchJSON(url, init) {
    const res = await fetch(url, {headers: {"Content-Type": "application/json"}, ...init});
    if (!res.ok) {
        const text = await res.text().catch(() => "");
        throw new Error(text || `HTTP ${res.status}`);
    }
    return res.status === 204 ? null : res.json();
}

function patchPollInStore(patch, pollId) {
    polls.update((list) => list.map((p) => (p.id === pollId ? patch(p) : p)));
}

export const users = writable([]);
export const polls = writable([]);
export const selectedUser = writable(null);
export const userVoteIds = writable(new Set());

export async function loadUsers() {
    const data = await fetchJSON(`${API_BASE}/users`);
    users.set(data ?? []);
}

export async function loadPolls() {
    const data = await fetchJSON(`${API_BASE}/polls`);
    polls.set(data ?? []);
}

export async function loadUserVotes(userId) {
    userVoteIds.set(new Set());
    if (!userId) return;
    const votes = await fetchJSON(`${API_BASE}/users/${userId}/votes`);
    const set = new Set((votes ?? []).map((v) => v.id).filter(Boolean));
    userVoteIds.set(set);
}

export function selectedOptionIdForPoll(poll, userVoteIdSet) {
    if (!poll || !Array.isArray(poll.options) || !userVoteIdSet) return null;
    for (const option of poll.options)
        for (const v of Array.isArray(option.votes) ? option.votes : [])
            if (v?.id && userVoteIdSet.has(v?.id))
                return option.id;
    return null;
}

export function selectUser(u) {
    selectedUser.set(u);
    if (u?.id)
        loadUserVotes(u.id).catch(() => userVoteIds.set(new Set()));
    else
        userVoteIds.set(new Set());
}

export async function createUser(payload) {
    const tempId = `tmp_${Date.now()}`;
    const optimistic = {
        id: tempId,
        username: payload.username,
        email: payload.email,
        polls: [],
        votes: [],
        __optimistic: true
    };

    users.update((arr) => [optimistic, ...arr]);

    try {
        const created = await fetchJSON(`${API_BASE}/users`, {
            method: "POST",
            body: JSON.stringify(payload),
        });

        users.update((arr) => [created, ...arr.filter((u) => u.id !== optimistic.id)]);
        selectedUser.set(created);

    } catch (e) {
        users.update((arr) => arr.filter((u) => u.id !== optimistic.id));
        throw e;
    }
}

export async function createPoll(payload) {
    const tempId = `tmp_${Date.now()}`;
    const optimistic = {
        id: tempId,
        question: payload.question,
        publishedAt: payload.publishedAt,
        validUntil: payload.validUntil,
        maxVotesPerUser: payload.maxVotesPerUser,
        isPrivate: payload.isPrivate,
        allowedVoters: payload.allowedVoters,
        options: payload.options.map((t, i) => ({
            id: `${tempId}_opt_${i}`, caption: t, presentationOrder: i, votes: []
        })),
        __optimistic: true,
    };

    polls.update((arr) => [optimistic, ...arr]);

    try {
        const created = await fetchJSON(`${API_BASE}/polls`, {
            method: "POST",
            body: JSON.stringify(payload),
        });

        polls.update((arr) => [created, ...arr.filter((p) => p.id !== tempId)]);

    } catch (e) {
        polls.update((arr) => arr.filter((p) => p.id !== tempId));
        throw e;
    }
}

export async function voteOnPoll(payload) {
    const snapshot = get(polls);

    patchPollInStore(
        (p) => ({
            ...p,
            options: p.options.map((o) =>
                o.id === payload.optionId ? {...o, votes: Number(o.votes ?? 0) + 1} : o
            ),
        }),
        payload.pollId
    );

    try {
        const createdVote = await fetchJSON(`${API_BASE}/polls/${payload.pollId}/votes`, {
            method: "POST",
            body: JSON.stringify(payload),
        });

        if (createdVote.id) {
            userVoteIds.update((set) => {
                const next = new Set(set);
                next.add(createdVote.id);
                return next;
            });
        }

        const fresh = await fetchJSON(`${API_BASE}/polls/${payload.pollId}`);
        polls.update((list) =>
            list.map((p) =>
                p.id === payload.pollId ? {
                    ...fresh,
                    options: (fresh.options ?? []).map((o) => ({...o, votes: (o.votes ?? [])})),
                } : p
            )
        );

    } catch (e) {
        polls.set(snapshot);
        throw e;
    }
}
