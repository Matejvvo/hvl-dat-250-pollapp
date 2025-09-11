<script lang="js">
    export let p;
    export let user;

    import {selectedOptionIdForPoll, userVoteIds, voteOnPoll} from "../lib/store.js";

    let isBusy = false;
    let error = null;

    $: selectedOptionId = selectedOptionIdForPoll(p, $userVoteIds);

    async function vote(e, optionId) {
        e.preventDefault();
        error = null;

        if (!user) {
            error = "Select a user first.";
            return;
        }

        isBusy = true;
        try {
            await voteOnPoll({
                voterId: user.id,
                optionId: optionId,
                pollId: p.id,
            });

        } catch (err) {
            error = "Could not cast vote.";
        } finally {
            isBusy = false;
        }
    }
</script>

<div class="card">
    <h3>{p.question}</h3>
    <ul>
        {#each p.options as o (o.id)}
            <li class:selected={o.id === selectedOptionId}>
                <button disabled={isBusy} on:click={(e) => vote(e, o.id)} class:selected={o.id === selectedOptionId}>
                    {#if o.id === selectedOptionId}✓ {/if}
                    {o.caption}
                </button>
                <span class="votes">{o.votes?.length ?? 0}</span>
            </li>
        {/each}
    </ul>
    {#if error}<p class="error">{error}</p>{/if}
</div>


<style>
    .card {
        border: 1px solid #ddd;
        border-radius: 12px;
        padding: 12px;
        background: #fff;
    }

    h3 {
        margin: 0 0 8px;
        font-size: 16px;
    }

    ul {
        display: grid;
        gap: 6px;
        list-style: none;
        padding: 0;
        margin: 0;
    }

    li {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 8px;
    }

    li.selected button {
        border-color: #0e2d59;
        background: #e8f0fe;
    }

    button {
        padding: 6px 10px;
        border-radius: 999px;
        border: 1px solid #aaa;
        background: #f8f8f8;
        cursor: pointer;
    }

    .votes {
        font-variant-numeric: tabular-nums;
        min-width: 2ch;
        text-align: right;
    }

    .error {
        color: #b00020;
        margin-top: 8px;
    }
</style>
