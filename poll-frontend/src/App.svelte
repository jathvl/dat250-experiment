<script lang="ts">
    import type { User } from "./lib/types";
    const APIROOT = "";

    let user = $state<User | {}>({});
    let isLoggedIn = $derived(Object.keys(user).length === 0);
    
    let loginEmail = $state("");
    let signupForm = $state({
        username: "",
        email: "",
    });
    
    let createPollForm = $state({
        question: "",
        answers: [""],
        durationHours: 1,
    });
    
    let polls = $state([]);

    async function handleSignin() {
      if (!loginEmail) return;
      
      const res = await fetch(`${APIROOT}/user/all`);
      if (!res.ok) return;
      
      const users: User[] = await res.json();
      
      for (const u of users) {
        if ("email" in u && u?.email && u.email === loginEmail) {
          user = u;
          loginEmail = "";
        }
      }
      
      await getPolls();
    }

    async function handleSignup() {
        if (!signupForm || !signupForm.username || !signupForm.email) return;

        const res = await fetch(`${APIROOT}/user/create`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(signupForm),
        });

        if (!res.ok) return;

        signupForm = { username: "", email: "" };
        user = await res.json();
        await getPolls();
    }
    
    function handleSignout() {
      user = {};
    }
    
    async function handleCreatePoll() {
      createPollForm.answers = createPollForm.answers.filter(x => x);
      if (!createPollForm.question || createPollForm.answers.length === 0 || createPollForm.durationHours < 1) return;
      
      const res = await fetch(`${APIROOT}/poll/create`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          creatorUserId: user.id,
          question: createPollForm.question,
          durationHours: createPollForm.durationHours,
          options: createPollForm.answers,
        }),
      });
      
      if (res.ok)
        createPollForm = {
          question: "",
          answers: [""],
          durationHours: 1,
        }
      
      await getPolls();
    }
    
    async function getPolls() {
      const res = await fetch(`${APIROOT}/poll/all`);
      const p = await res.json();
      p.sort((a, b) => new Date(b.validUntil) - new Date(a.validUntil));
      polls = p;
    }
    
    async function voteFor(optionId: number) {
      await fetch(`${APIROOT}/vote`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          userId: user.id,
          voteOptionId: optionId,
        }),
      });
      
      await getPolls();
    }
    
    async function deletePoll(pollId: number) {
      await fetch(`${APIROOT}/poll/${pollId}`, { method: "DELETE" });
      await getPolls();
    }
</script>

<main>
    {#if (isLoggedIn)}
        <form>
            <label for="email">Email:</label>
            <input name="email" type="text" bind:value={loginEmail} />
            <br />
    
            <button type="button" onclick={handleSignin}>Sign in</button>
        </form>
        
        <br />
    
        <form>
            <label for="username">Username:</label>
            <input name="username" type="text" bind:value={signupForm.username} />
            <br />
    
            <label for="email">Email:</label>
            <input name="email" type="text" bind:value={signupForm.email} />
            <br />
    
            <button type="button" onclick={handleSignup}>Sign up</button>
        </form>
    {:else}
        <button type="button" onclick={handleSignout}>Sign out</button>
        <h1>Hello, {user!.username}!</h1>
        
        <form>
            <h2>Create poll</h2>
            
            <label for="question">Question:</label>
            <input name="question" type="text" bind:value={createPollForm.question} />
            <br />
            
            <label for="durationHours">Duration (hours):</label>
            <input name="durationHours" type="number" bind:value={createPollForm.durationHours} />
            <br />
            
            {#each createPollForm.answers as _, index}
              <input
                type="text"
                bind:value={createPollForm.answers[index]}
                placeholder="Option {index + 1}"
              />
              <button type="button" onclick={() => createPollForm.answers = createPollForm.answers.filter((_, i) => i !== index)}>
                Remove
              </button>
              <br />
            {/each}
            
            <button type="button" onclick={() => createPollForm.answers.push("")}>
              Add Option
            </button>
            <br />
            <br />
            
            <button type="button" onclick={handleCreatePoll}>Create poll</button>
        </form>
        
        {#each polls as poll}
            <div>
                <h3>
                    {poll.question}
                    {#if new Date(poll.validUntil) < new Date()}
                        (expired)
                    {:else}
                        (expires {poll.validUntil})
                    {/if}
                    <button type="button" onclick={async () => await deletePoll(poll.id)}>Delete poll</button>
                </h3>
                
                <ol>
                    {#each poll.options as option}
                        <li>
                            {option.caption} ({option.votes.length} votes)
                            {#if option.votes.filter(v => v.user === user.id || v.user?.id === user.id).length !== 0}
                                (your previous choice)
                            {:else if new Date(poll.validUntil) > new Date()}
                                <button onclick={async () => await voteFor(option.id)}>Vote</button>
                            {/if}
                        </li>
                    {/each}
                </ol>
            </div>
        {/each}
    {/if}
</main>

<style>
</style>
