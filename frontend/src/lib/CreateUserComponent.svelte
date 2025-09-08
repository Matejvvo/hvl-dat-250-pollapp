<script>
    let username = "";
    let email = "";
    let password = "";

    function createUser() {

    }
</script>

<main>
    <h2>Create User</h2>
    <form on:submit|preventDefault={createUser}>
        <div>
            <span>Username</span>
            <input bind:value={username} type="text" placeholder="Enter your username"/>
        </div>
        <div>
            <span>Email</span>
            <input bind:value={email} type="email" placeholder="Enter your email"/>
        </div>
        <div>
            <span>Password</span>
            <input bind:value={password} type="password" placeholder="Enter your password"/>
        </div>
        <div>
            <button type="submit">Create User</button>
        </div>
    </form>
</main>
