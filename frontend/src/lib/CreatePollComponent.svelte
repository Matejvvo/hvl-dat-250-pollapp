<script>
    let question = "";
    let validUntil = "";
    let voteOption = "";

    let options = ["asd", "asd", "asd"];

    function createPoll() {

    }

    function addOption() {

    }

    function delOption() {

    }
</script>

<main>
    <h2>Create Poll</h2>
    <form>
        <div class="side-by-side">
            <div>
                <div>
                    <span>Question</span>
                    <input bind:value={question} type="text" placeholder="Enter question"/>
                </div>
                <div>
                    <span>Valid Until</span>
                    <input bind:value={validUntil} type="date" placeholder="Enter date"/>
                </div>
            </div>
            <div>
                <span>Vote Options</span>
                <ul>
                    {#each options as option}
                        <li>
                            <span>{option}</span>
                            <button on:click={delOption}>⌫</button>
                        </li>
                    {/each}
                    <li>
                        <input type="text" bind:value={voteOption} placeholder="Enter vote option"/>
                        <button on:click={addOption} type="button">Add Option</button>
                    </li>
                </ul>
            </div>
        </div>
        <div>
            <button on:click={createPoll} type="submit">Create Poll</button>
        </div>
    </form>
</main>
