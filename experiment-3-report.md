# Report

[GitHub repository](https://github.com/Matejvvo/hvl-dat-250-pollapp/)

[GitHub: frontend](https://github.com/Matejvvo/hvl-dat-250-pollapp/tree/main/frontend) 

[GitHub: backend](https://github.com/Matejvvo/hvl-dat-250-pollapp/tree/main/backend)

For this assignment I had to choose and learn some frontend framework. Based on the recommendation, I chose Svelte with JS.

I started with building the 3 specified components. Later the fetching logic started to be too complex for these components. So I searched the internet and found out I should create a store file for all the logic.

During programming of the logic, I noticed my implementation of serializing the domain complicates the fetching a bit. For example, matching users votes with the votes on the poll. Maybe it would be better to design the domain in another better way, but for the sake a stuck to the original domain.

Crafting the views with Svelte was easy. Since I have no knowledge of CSS, I asked ChatGPT for a simple styling.

For future improvements, I should implement proper error handling both in backend and frontend. More UI testing would be required and I should look once more into the domain design.
