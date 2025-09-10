<script lang="js">
    import {onMount} from "svelte";

    import {loadPolls, loadUsers, polls, selectedUser, selectUser, users} from "./lib/store.js";

    import CreateUser from "./components/CreateUser.svelte";
    import CreatePoll from "./components/CreatePoll.svelte";
    import CastVote from "./components/CastVote.svelte";
    import UserItem from "./components/UserItem.svelte";

    onMount(() => {
        loadUsers();
        loadPolls();
    });
</script>

<main>
    <h1>Polling App</h1>

    <section>
        <h2>Create a Users</h2>
        <CreateUser/>
    </section>

    <section>
        <h2>Users</h2>
        <div class="users">
            {#if $users.length === 0}
                <p>No users yet.</p>
            {:else}
                {#each $users as u (u.id)}
                    <UserItem user={u} isSelected={$selectedUser?.id === u.id} onSelect={selectUser}/>
                {/each}
            {/if}
        </div>
    </section>

    <section>
        <h2>Create a Poll</h2>
        {#if $selectedUser}
            <CreatePoll user={$selectedUser}/>
        {:else}
            <p class="hint">Select or create a user to create a poll and vote.</p>
        {/if}

    </section>

    <section>
        <h2>Polls</h2>
        {#if $polls.length === 0}
            <p>No polls yet.</p>
        {:else}
            <div class="polls">
                {#each $polls as p (p.id)}
                    <CastVote {p} user={$selectedUser}/>
                {/each}
            </div>
        {/if}
    </section>
</main>

<style>
    :global(html, body) {
        margin: 0;
        padding: 0;
    }

    main {
        max-width: 900px;
        margin: 0 auto;
        padding: 24px;
        font-family: system-ui, -apple-system, Segoe UI, Roboto, Arial, sans-serif;
    }

    h1 {
        margin: 0 0 8px;
    }

    section {
        margin: 24px 0;
    }

    .users {
        display: flex;
        gap: 8px;
        flex-wrap: wrap;
        margin-bottom: 12px;
    }

    .polls {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
        gap: 12px;
    }

    .hint {
        color: #555;
    }
</style>
