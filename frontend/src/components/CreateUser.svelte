<script>
    export let onCreateUserCallback;

    let username = "";
    let email = "";
    let password = "";

    $: isUsernameValid = username.trim().length >= 4;
    $: isEmailValid = email.trim().length >= 4;
    $: isPasswordValid = password.trim().length >= 4;
    $: isFormValid = isUsernameValid && isEmailValid && isPasswordValid;

    function createUser() {
        if (!isFormValid) return null;

        const payload = {
            username: username,
            email: email,
        }

        console.log(payload);
        onCreateUserCallback?.(payload);

        username = "";
        email = "";
        password = "";
    }
</script>

<main>
    <h2>Create User</h2>

    <form class="card" on:submit|preventDefault={createUser}>
        <div class="side-by-side">
            <div class="section">
                <label class="row">
                    <span>Username</span>
                    <input bind:value={username} type="text" placeholder="Enter your username" required/>
                </label>

                <label class="row">
                    <span>Email</span>
                    <input bind:value={email} type="email" placeholder="Enter your email" required/>
                </label>

                <label class="row">
                    <span>Password</span>
                    <input bind:value={password} type="password" placeholder="Enter your password" required/>
                </label>
            </div>
        </div>

        <div class="actions">
            <button type="submit" disabled={!isFormValid}>Create User</button>
        </div>
    </form>
</main>
