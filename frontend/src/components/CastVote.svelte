<script>
    export let user;
    export let poll;
</script>

<main>
    <form class="card poll">
        <div class="section">
            <div class="row-inline">
                <h4 class="question">{poll.question}</h4>

                {#if poll.creator.id === user.id}
                    <button class="btn btn-ghost btn-sm" type="button">Remove Poll</button>
                {/if}
            </div>

            {#each poll.options as option (option.id)}
                <div class="option-row"
                     style="--pct: {(option.votes.length / Math.max(1, poll.options.reduce((a,o)=>a+o.votes.length,0))) * 100}%">
                    <span class="label">{option.caption}</span>

                    <button class="vote-btn btn-sm" type="button">Vote!</button>

                    <span class="count">{option.votes.length} votes</span>
                </div>
            {/each}

        </div>
    </form>
</main>
