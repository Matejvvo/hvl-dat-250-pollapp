import {get, writable} from "svelte/store";

const API_BASE = "/api"

async function fetchJSON(url, init) {
    const res = await fetch(url, {headers: {"Content-Type": "application/json"}, ...init});
    if (!res.ok) {
        const text = await res.text().catch(() => "");
        throw new Error(text || `HTTP ${res.status}`);
    }
    if (res.status === 204) return null;
    return res.headers.get("content-type")?.includes("application/json") ? res.json() : res.text();
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
    try {
        const created = await fetchJSON(`${API_BASE}/users`, {
            method: "POST",
            body: JSON.stringify(payload),
        });

        users.update((arr) => [created, ...arr.filter((u) => u.id !== created.id)]);
        selectedUser.set(null)
        selectedUser.set(created);
        selectUser(created);

    } catch (e) {
        throw e;
    }
}

export async function createPoll(payload) {
    try {
        const created = await fetchJSON(`${API_BASE}/polls`, {
            method: "POST",
            body: JSON.stringify(payload),
        });

        polls.update((arr) => [created, ...arr.filter((p) => p.id !== created.id)]);

    } catch (e) {
        throw e;
    }
}

export async function voteOnPoll(payload) {
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
        throw e;
    }
}

export async function removeVoteFromPoll({pollId, optionId}) {
    let voteId = null;
    const poll = get(polls).find((p) => p.id === pollId);
    if (poll) {
        const opt = (poll.options || []).find((o) => o.id === optionId);
        if (opt) {
            for (const v of Array.isArray(opt.votes) ? opt.votes : []) {
                if (v?.id && get(userVoteIds).has(v?.id)) {
                    voteId = v?.id;
                    break;
                }
            }
        }
    }
    if (!voteId) throw new Error("No matching vote to remove for this user on this option.");

    try {
        await fetchJSON(`${API_BASE}/votes/${voteId}`, {method: "DELETE"});

        const fresh = await fetchJSON(`${API_BASE}/polls/${pollId}`);

        polls.update((list) =>
            list.map((p) =>
                p.id === pollId ? {
                        ...fresh,
                        options: (fresh.options ?? []).map((o) => ({...o, votes: (o.votes ?? [])})),
                    } : p
            )
        );

        userVoteIds.update((set) => {
            const next = new Set(set);
            next.delete(voteId);
            return next;
        });

    } catch (e) {
        throw e;
    }
}
