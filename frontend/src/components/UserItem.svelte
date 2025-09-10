<script lang="js">
    export let user;
    export let isSelected = false;
    export let onSelect;
</script>

<button class:selected={isSelected} on:click={() => onSelect?.(user)}>
    {user.username}
</button>

<style>
    button {
        padding: 8px 10px;
        border-radius: 999px;
        border: 1px solid #999;
        background: white;
        cursor: pointer;
    }

    button.selected {
        background: #e8f0fe;
        border-color: #0e2d59;
        color: #1e3a8a;
    }
</style>
