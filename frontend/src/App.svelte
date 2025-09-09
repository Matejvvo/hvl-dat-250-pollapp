<script>
    import {onMount} from "svelte";

    import CreatePoll from "./components/CreatePoll.svelte";
    import CreateUser from "./components/CreateUser.svelte";
    import CastVote from "./components/CastVote.svelte";
    import UserItem from "./components/UserItem.svelte";

    const API_BASE = "http://localhost:8080/api";

    let users = [];
    let polls = [];
    let selectedUser = null;

    async function fetchUsers() {
        const request = {
            method: "GET",
            headers: {Accept: "application/json; charset=UTF-8"},
        }

        const url = `${API_BASE}/users`;

        try {
            const response = await fetch(url, request);
            if (!response.ok) return false;

            const data = await response.json();
            if (data == null || data.length === 0) return false;
            for (let user of data)
                users = [...users, user];

            return true
        } catch (error) {
            console.error(error);
        }

        return false;
    }

    async function fetchPolls() {
        const request = {
            method: "GET",
            headers: {Accept: "application/json; charset=UTF-8"},
        }

        const url = `${API_BASE}/polls`;

        try {
            const response = await fetch(url, request);
            if (!response.ok) return false;

            const data = await response.json();
            if (data == null || data.length === 0) return false;
            for (let poll of data)
                polls = [...polls, poll];

            return true;
        } catch (error) {
            console.error(error);
        }

        return false;
    }

    async function handleCreateUserCallback(user) {
        const request = {
            method: "POST",
            headers: {"Content-Type": "application/json; charset=UTF-8"},
            body: JSON.stringify(user),
        }

        const url = `${API_BASE}/users`;

        try {
            const response = await fetch(url, request);
            if (!response.ok) return false;

            const data = await response.json();
            if (data == null) return false;
            users = [...users, data];

            return true;
        } catch (error) {
            console.error(error);
        }

        return false;
    }

    async function handleCreatePollCallback(poll, user) {
        console.log(poll);

        const request = {
            method: "POST",
            headers: {"Content-Type": "application/json; charset=UTF-8"},
            body: JSON.stringify(poll),
        }

        const url = `${API_BASE}/polls`;

        try {
            const response = await fetch(url, request);
            if (!response.ok) return false;

            const data = await response.json();
            if (data == null) return false;
            polls = [...polls, data];
            users.find(u => u.id === user.id).polls = [...users.find(u => u.id === user.id).polls, data];

            return true;
        } catch (error) {
            console.error(error);
        }

        return false;
    }

    function handleUserSelectChangeCallback(user) {
        if (selectedUser != null && user === selectedUser) selectedUser = null;
        else selectedUser = user;
    }


    async function handleDeletePollCallback(poll) {
        const request = {
            method: "DELETE",
        }

        const url = `${API_BASE}/polls/${poll.id}`;

        try {
            const response = await fetch(url, request);
            if (!response.ok) return false;

            polls = polls.filter(p => p.id !== poll.id);

            return true;
        } catch (error) {
            console.error(error);
        }

        return false;
    }

    async function handleCastVoteCallback(poll, user, option) {
        const body = {
            voterId: user.id,
            optionId: option.id,
        }

        const request = {
            method: "POST",
            headers: {"Content-Type": "application/json; charset=UTF-8"},
            body: JSON.stringify(body),
        }

        const url = `${API_BASE}/polls/${poll.id}/votes`;

        try {
            const response = await fetch(url, request);
            if (!response.ok) return false;

            const text = await response.text();
            const data = text ? JSON.parse(text) : null;
            if (data == null) return false;

            const updateById = (arr, id, updater) => arr.map(item => (item.id === id ? updater(item) : item));
            users = updateById(users, user.id, u => ({
                ...u, votes: [...(u.votes ?? []), data],
            }));
            polls = updateById(polls, poll.id, p => ({
                ...p, options: updateById(p.options ?? [], option.id, o => ({
                    ...o, votes: [...(o.votes ?? []), data],
                })),
            }));
            selectedUser = users.find(u => u.id === user.id);

            return true;

        } catch (error) {
            console.error(error);
        }

        return false;
    }

    async function handleRemoveVoteCallback(poll, user, vote, option) {
        const request = {
            method: "DELETE",
        }

        const url = `${API_BASE}/votes/${vote.id}`;

        try {
            const response = await fetch(url, request);
            if (!response.ok) return false;

            const updateById = (arr, id, updater) => arr.map(item => (item.id === id ? updater(item) : item));
            users = updateById(users, user.id, u => ({
                ...u, votes: (u.votes ?? []).filter(v => v.id !== vote.id)
            }));
            polls = updateById(polls, poll.id, p => ({
                ...p, options: updateById(p.options ?? [], option.id, o => ({
                    ...o, votes: (o.votes ?? []).filter(v => v.id !== vote.id)
                })),
            }));
            selectedUser = users.find(u => u.id === user.id);

            return true;

        } catch (error) {
            console.error(error);
        }

        return false;
    }

    onMount(() => {
        users = [];
        polls = [];
        fetchUsers();
        fetchPolls();
    });
</script>

<main>
    <h1>Poll App</h1>

    <div class="side-by-side">
        <CreateUser onCreateUserCallback={handleCreateUserCallback}></CreateUser>
        {#if selectedUser}
            <CreatePoll user={selectedUser} onCreatePollCallback={handleCreatePollCallback}></CreatePoll>
        {:else}
            <div></div>
        {/if}
    </div>

    {#if users.length !== 0}
        <h2>Selected User: {selectedUser?.username ?? "none"}</h2>
        <div class="users-grid">
            {#each users as user}
                <UserItem user={user}
                          selectedUser={selectedUser}
                          onUserSelectChangeCallback={handleUserSelectChangeCallback}>

                </UserItem>
            {/each}
        </div>
    {/if}

    {#if polls.length !== 0}
        <h2>Voting as: {selectedUser?.username ?? "none"}</h2>
        <div class="polls-grid">
            {#each polls as poll}
                <CastVote user={selectedUser} poll={poll}
                          onCastVoteCallback={handleCastVoteCallback}
                          onDeletePollCallback={handleDeletePollCallback}
                          onRemoveVoteCallback={handleRemoveVoteCallback}>

                </CastVote>
            {/each}
        </div>
    {/if}
</main>
