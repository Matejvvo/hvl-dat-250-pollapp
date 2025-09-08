<script>
    export let user;
    export let onCreatePollCallback;

    let question = "";
    let validUntil = "";
    let voteOption = "";
    let options = [];

    const hasUser = user != null && user.id != null;
    const today = new Date().toISOString().slice(0, 10);
    $: isQuestionValid = question.trim().length >= 4;
    $: isDateValid = validUntil && validUntil >= today;
    $: hasEnoughOptions = options.length >= 2;
    $: isFormValid = isQuestionValid && isDateValid && hasEnoughOptions && hasUser;

    function createPoll() {
        if (!isFormValid) return null;

        const payload = {
            question: question.trim(),
            maxVotesPerUser: 1,
            isPrivate: false,
            creatorId: user.id,
            publishedAt: today,
            validUntil: validUntil,
            options: options,
        }

        console.log(payload);
        onCreatePollCallback?.(payload);

        question = "";
        validUntil = "";
        options = [];
        voteOption = "";
    }

    /**
     * @param {event} e
     */
    function addOption(e) {
        e?.preventDefault();

        const value = voteOption.trim();
        if (!value) return;

        const exists = options.some(o => o.toLowerCase() === value.toLowerCase());
        if (exists) return;

        options = [...options, value];
        voteOption = "";
    }

    /**
     * @param {number} index
     */
    function delOption(index) {
        options = options.filter((_, i) => i !== index);
    }

</script>

<main>
    <h2>Create Poll as {user.username}</h2>

    <form class="card" on:submit|preventDefault={createPoll}>
        <div class="side-by-side">
            <!-- left column -->
            <div class="section">
                <label class="row">
                    <span>Question</span>
                    <input bind:value={question} type="text" placeholder="Enter question" required/>
                </label>

                <label class="row">
                    <span>Valid Until</span>
                    <input bind:value={validUntil} type="date" required/>
                </label>
            </div>

            <!-- right column -->
            <div class="section">
                <label class="row">
                    <span>Vote Options</span>

                    <ul class="options-list">
                        {#if options.length === 0}
                            <li>
                                <p style="font-size: .7rem">No options yet. Add at least two.</p>
                            </li>
                        {/if}
                        {#each options as option, i}
                            <li>
                                <span>{option}</span>
                                <button class="btn btn-ghost btn-sm" on:click|preventDefault={() => delOption(i)}>✖</button>
                            </li>
                        {/each}
                    </ul>
                </label>

                <div class="add-row">
                    <input bind:value={voteOption} type="text" placeholder="Enter vote option" on:keydown={(e) => {
                        if (e.key === 'Enter') addOption(e);
                    }}/>
                    <button class="btn btn-ghost" on:click|preventDefault={addOption} disabled={!voteOption.trim()}>Add</button>
                </div>
            </div>
        </div>

        <div class="actions">
            <button type="submit" disabled={!isFormValid}>Create Poll</button>
        </div>
    </form>
</main>
