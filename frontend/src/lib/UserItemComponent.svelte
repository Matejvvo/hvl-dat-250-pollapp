<script>
    export let user;
    import { createEventDispatcher } from "svelte";

    const dispatch = createEventDispatcher();

    function handleClick() {
        dispatch("select", { user });
    }
</script>

<button class="user-item" on:click={handleClick}>
    {user}
</button>