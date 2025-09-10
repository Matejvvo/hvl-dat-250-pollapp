<script lang="js">
    export let user;

    import {createPoll} from "../lib/store.js";

    let question = "";
    let optionsText = "";
    let validUntil = "";
    let isBusy = false;
    let error = null;

    const today = new Date().toISOString().slice(0, 10);

    async function submit(e) {
        e.preventDefault();
        error = null;

        const opts = optionsText
            .split("\n")
            .map(s => s.trim())
            .filter(Boolean);

        if (!question.trim()) {
            error = "Enter a question.";
            return;
        }
        if (opts.length < 2) {
            error = "Enter at least two options (one per line).";
            return;
        }
        if (!validUntil || validUntil < today) {
            error = "Enter date after today.";
            return;
        }

        isBusy = true;
        try {
            await createPoll({
                question: question.trim(),
                maxVotesPerUser: 1,
                isPrivate: false,
                creatorId: user.id,
                publishedAt: (new Date(today + "T00:00:00Z")).toISOString(),
                validUntil: (new Date(validUntil + "T00:00:00Z")).toISOString(),
                options: opts,
            });
            question = "";
            optionsText = "";
            validUntil = "";

        } catch (err) {
            error = err?.message ?? "Could not create poll.";
        } finally {
            isBusy = false;
        }
    }
</script>

<form on:submit|preventDefault={submit} class="card">
    <label>
        <span>Question</span>
        <input bind:value={question} placeholder="Your favorite language?"/>
    </label>
    <label>
        <span>Options (one per line)</span>
        <textarea bind:value={optionsText} rows="3" placeholder="enter option"></textarea>
    </label>
    <label>
        <span>Valid Until</span>
        <input bind:value={validUntil} type="date"/>
    </label>
    <button disabled={isBusy}>Create Poll</button>
    {#if error}<p class="error">{error}</p>{/if}
</form>

<style>
    .card {
        display: grid;
        gap: 8px;
        padding: 12px;
        border: 1px solid #ddd;
        border-radius: 10px;
    }

    input, textarea {
        padding: 8px;
        border: 1px solid #ccc;
        border-radius: 8px;
    }

    button {
        width: fit-content;
        padding: 8px 12px;
        border-radius: 8px;
    }

    .error {
        color: #b00020;
    }
</style>
