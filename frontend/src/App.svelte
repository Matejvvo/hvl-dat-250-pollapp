<script>
    import CreatePoll from "./components/CreatePoll.svelte";
    import CreateUser from "./components/CreateUser.svelte";
    import CastVote from "./components/CastVote.svelte";
    import UserItem from "./components/UserItem.svelte";

    let users = [
        {id: 1, username: "alice", email: "alice@example.com"},
        {id: 2, username: "bob", email: "bob@example.com"},
        {id: 3, username: "charlie", email: "charlie@example.com"},
        {id: 4, username: "charlie", email: "charlie@example.com"},
        {id: 5, username: "charlie", email: "charlie@example.com"},
        {id: 6, username: "charlie", email: "charlie@example.com"},
        {id: 7, username: "charlie", email: "charlie@example.com"},
    ];
    let testVoteOptions = [
        {id: 1, caption: "yes", presentationOrder: 0, poll: null, votes: [1, 2, 3, 4, 5]},
        {id: 2, caption: "no", presentationOrder: 1, poll: null, votes: [1, 2, 3]},
        {id: 3, caption: "maybe", presentationOrder: 2, poll: null, votes: []},
        {id: 4, caption: "1", presentationOrder: 3, poll: null, votes: []},
        {id: 5, caption: "2", presentationOrder: 4, poll: null, votes: [1]},
        {id: 6, caption: "3", presentationOrder: 5, poll: null, votes: []},
    ]
    let polls = [
        {id: 1, question: "Pineapple on pizza?", publishedAt: Date.now(), validUntil: Date.UTC(2026, 1, 1),
            maxVotesPerUser: 1, isPrivate: false, allowedVoters: [], creator: users[1], options: testVoteOptions},
        {id: 2, question: "Pineapple on pizza?", publishedAt: Date.now(), validUntil: Date.UTC(2026, 1, 1),
            maxVotesPerUser: 1, isPrivate: false, allowedVoters: [], creator: users[1], options: testVoteOptions},
        {id: 3, question: "Pineapple on pizza?", publishedAt: Date.now(), validUntil: Date.UTC(2026, 1, 1),
            maxVotesPerUser: 1, isPrivate: false, allowedVoters: [], creator: users[1], options: testVoteOptions},
    ];
    let selectedUser = null;

    // /**
    //  * @param {user} user
    //  */
    function handleCreateUserCallback(user) {
        user.id = Math.floor(Math.random() * 100);
        users = [...users, user];
    }

    // /**
    //  * @param {poll} poll
    //  */
    function handleCreatePollCallback(poll) {
        polls = [...polls, poll];
    }

    // /**
    //  * @param {user} user
    //  */
    function handleUserSelectChangeCallback(user) {
        if (selectedUser != null && user === selectedUser)
            selectedUser = null;
        else
            selectedUser = user;
    }
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
                <UserItem user={user} selectedUser={selectedUser} onUserSelectChangeCallback={handleUserSelectChangeCallback}></UserItem>
            {/each}
        </div>
    {/if}

    {#if selectedUser && polls.length !== 0}
        <h2>Voting as: {selectedUser.username}</h2>
        <div class="polls-grid">
            {#each polls as poll}
                <CastVote user={selectedUser} poll={poll}></CastVote>
            {/each}
        </div>
    {/if}
</main>
