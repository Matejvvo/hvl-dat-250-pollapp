<script>
    import CreatePollComponent from "./lib/CreatePollComponent.svelte";
    import CreateUserComponent from "./lib/CreateUserComponent.svelte";
    import CastVoteComponent from "./lib/CastVoteComponent.svelte";
    import UserItemComponent from "./lib/UserItemComponent.svelte";

    let users = ["asd", "asd", "asd"];

    let selectedUser = "";

    function selectUser(user) {

    }

    function handleSelect(event) {
        selectedUser = event.detail.user;
    }
</script>

<main>
    <h1>Poll App</h1>

    <div class="side-by-side">
        <CreateUserComponent></CreateUserComponent>
        <CreatePollComponent></CreatePollComponent>
    </div>

    <h2>Users</h2>
    <div>
        {#each users as user}
            <UserItemComponent user={user} on:select={handleSelect}></UserItemComponent>
        {/each}
    </div>

    {#if selectedUser}
        <h2>Voting as: {selectedUser}</h2>
        <CastVoteComponent user={selectedUser}></CastVoteComponent>
    {/if}
</main>

<style>
</style>