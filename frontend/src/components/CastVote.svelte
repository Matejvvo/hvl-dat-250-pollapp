<script>
    import {onMount} from "svelte";

    export let user;
    export let poll;
    export let onDeletePollCallback;
    export let onCastVoteCallback;
    export let onRemoveVoteCallback;

    $: firstUserVote = (user?.votes ?? [])[0] ?? null;
    $: firstUserVoteId = firstUserVote?.id ?? null;

    $: selectedOptionId = firstUserVoteId != null
        ? poll?.options?.find((o) => (o.votes ?? []).some((v) => v.id === firstUserVoteId))?.id ?? null
        : null;

    $: caDelete = !!(user && user.polls?.some((p) => p.id === poll?.id));

    $: pctMax = Array.isArray(poll?.options) && poll.options.length > 0
        ? Math.max(1, poll.options.reduce((a, o) => a + ((o.votes?.length) ?? 0), 0))
        : 0;

    function deletePoll() {
        onDeletePollCallback?.(poll)
    }

    async function castVote(option) {
        if (!option) return;

        if (selectedOptionId === option.id) {
            const ok = await onRemoveVoteCallback?.(poll, user, firstUserVote, option);
            if (!ok) return;
        } else {
            const ok = await onCastVoteCallback?.(poll, user, option);
            if (!ok) return;
        }
    }
</script>

<main>
    <form class="card poll">
        <div class="section">
            <div class="row-inline">
                <h4>{poll.question}</h4>

                {#if caDelete}
                    <button class="btn btn-ghost btn-sm" on:click|preventDefault={deletePoll}>
                        Remove Poll
                    </button>
                {/if}
            </div>

            {#if poll.options != null && poll.options.length > 0}
                {#each poll.options as option (option.id)}
                    <div class="option-row" class:selected={selectedOptionId === option.id}
                         style="--pct: {option.votes.length / pctMax * 100}%">
                        <span class="label">{option.caption}</span>

                        {#if user != null}
                            <button class="vote-btn btn-sm" on:click|preventDefault={() => castVote(option)}>
                                Vote!
                            </button>
                        {/if}

                        <span class="count">{option.votes.length} votes</span>
                    </div>
                {/each}
            {/if}
        </div>
    </form>
</main>

<style>
    .option-row.selected {
        color: #1e3a8a;
        background-color: #e0f7fa;
        border: 2px solid #00acc1;
    }
</style>
