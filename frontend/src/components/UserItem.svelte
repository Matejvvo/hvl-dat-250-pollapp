<script>
    export let user;
    export let selectedUser;
    export let onUserSelectChangeCallback;

    function selectUser() {
        onUserSelectChangeCallback?.(user);
    }

    $: isSelected = user.id === selectedUser?.id;
</script>

<main>
    <button on:click={selectUser} class:selected={isSelected}>
        <div><strong>ID:</strong> {user.id}</div>
        <div><strong>Username:</strong> {user.username}</div>
        <div><strong>Email:</strong> {user.email}</div>
    </button>
</main>

<style>
    div {
        padding: 2px 0;
    }

    button {
        margin: 10px;
        min-width: 170px;
        min-height: 90px;
    }

    button.selected {
        background: #e8f0fe;
        border: 1px solid #0e2d59;
        color: #1e3a8a;
    }
</style>
