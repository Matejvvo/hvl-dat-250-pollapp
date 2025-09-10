<script lang="js">
    import {createUser} from "../lib/store.js";

    let username = "";
    let email = "";
    let isBusy = false;
    let error = null;

    async function submit(e) {
        e.preventDefault();
        error = null;

        if (!username.trim()) {
            error = "Please enter a username";
            return;
        }
        if (!email.trim()) {
            error = "Please enter an email";
            return;
        }

        isBusy = true;
        try {
            await createUser({
                username: username.trim(),
                email: email.trim(),
            });
            username = "";
            email = "";

        } catch (err) {
            error = err?.message ?? "Could not create user";
        } finally {
            isBusy = false;
        }
    }
</script>

<form on:submit|preventDefault={submit} class="card">
    <label>
        <span>Username</span>
        <input bind:value={username} placeholder="Jane Doe"/>
    </label>
    <label>
        <span>Email</span>
        <input bind:value={email} placeholder="jane.doe@me.com"/>
    </label>
    <button disabled={isBusy}>Create User</button>
    {#if error}<p class="error">{error}</p>{/if}
</form>

<style>
    .card {
        display: flex;
        gap: 8px;
        align-items: center;
    }

    input {
        padding: 8px;
        border: 1px solid #ccc;
        border-radius: 6px;
    }

    button {
        padding: 8px 12px;
        border-radius: 6px;
    }

    .error {
        color: #b00020;
        margin: 0 0 0 8px;
    }
</style>
