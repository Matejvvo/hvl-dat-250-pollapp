#!/usr/bin/env bash
set -euo pipefail

# ==== Config ====
BASE_URL="${BASE_URL:-http://localhost:8080}"
USERS_URL="$BASE_URL/api/users"
POLLS_URL="$BASE_URL/api/polls"

ts_now_utc() {
  date -u +"%Y-%m-%dT%H:%M:%SZ"
}

ts_future_utc() {
  if date --version >/dev/null 2>&1; then
    # GNU date (Linux)
    date -u -d "+90 days" +"%Y-%m-%dT%H:%M:%SZ"
  else
    # BSD date (macOS)
    date -u -v+90d +"%Y-%m-%dT%H:%M:%SZ"
  fi
}

say() { printf "\n== %s ==\n" "$*"; }
curl_json() { curl -sS -H "Content-Type: application/json" "$@"; }

# ---- 1) Create Users ----
say "Creating users: Alice and Bob"
ALICE_JSON=$(curl_json -X POST "$USERS_URL" \
  -d '{"username":"alice","email":"alice@example.com","password":"not-used"}')
ALICE_ID=$(jq -r '.id' <<<"$ALICE_JSON")

BOB_JSON=$(curl_json -X POST "$USERS_URL" \
  -d '{"username":"bob","email":"bob@example.com","password":"not-used"}')
BOB_ID=$(jq -r '.id' <<<"$BOB_JSON")

echo "Alice ID: $ALICE_ID"
echo "Bob ID:   $BOB_ID"

# ---- 2) Create a Poll (by Alice) ----
say "Creating poll (by Alice)"
PUBLISHED_AT=$(ts_now_utc)
VALID_UNTIL=$(ts_future_utc)

POLL_JSON=$(curl_json -X POST "$POLLS_URL" -d "{
  \"question\":\"What is your favorite color?\",
  \"maxVotesPerUser\":1,
  \"isPrivate\":false,
  \"creatorId\":\"$ALICE_ID\",
  \"publishedAt\":\"$PUBLISHED_AT\",
  \"validUntil\":\"$VALID_UNTIL\",
  \"options\":[\"yellow\"]
}")
POLL_ID=$(jq -r '.id' <<<"$POLL_JSON")
echo "Poll ID: $POLL_ID"

# ---- 3) Add Vote Options ----
say "Adding options: Blue, Green, Red"

BLUE_JSON=$(curl_json -X POST "$POLLS_URL/$POLL_ID/options" -d '{"caption":"Blue"}')
BLUE_ID=$(jq -r '.id' <<<"$BLUE_JSON")
echo "Blue option ID:  $BLUE_ID"

GREEN_JSON=$(curl_json -X POST "$POLLS_URL/$POLL_ID/options" -d '{"caption":"Green"}')
GREEN_ID=$(jq -r '.id' <<<"$GREEN_JSON")
echo "Green option ID: $GREEN_ID"

RED_JSON=$(curl_json -X POST "$POLLS_URL/$POLL_ID/options" -d '{"caption":"Red"}')
RED_ID=$(jq -r '.id' <<<"$RED_JSON")
echo "Red option ID:   $RED_ID"

say "Listing poll options"
curl -sS "$POLLS_URL/$POLL_ID/options" | jq -r '.[] | "\(.id)  \(.caption)"'

# ---- 4) (Optional) Add allowed voter (works even if poll is public) ----
say "Optionally whitelisting Bob as allowed voter (safe even if poll is public)"
curl -sS -X POST "$POLLS_URL/$POLL_ID/voters/$BOB_ID" >/dev/null
echo "Bob added to allowed voters (no-op for public polls)."

say "Listing allowed voters"
curl -sS "$POLLS_URL/$POLL_ID/voters" | jq -r '.[] | "\(.id)  \(.username) <\(.email)>"'

# ---- 5) Bob casts a vote for Blue ----
say "Bob casts a vote for Blue"
VOTE_BODY=$(jq -n --arg voter "$BOB_ID" --arg opt "$BLUE_ID" \
  '{voterId:$voter, optionId:$opt}')

VOTE_JSON=$(curl_json -X POST "$POLLS_URL/$POLL_ID/votes" -d "$VOTE_BODY")
VOTE_ID=$(jq -r '.id' <<<"$VOTE_JSON")
echo "Vote ID: $VOTE_ID"

# ---- 6) List poll votes ----
say "Listing votes for the poll"
curl -sS "$POLLS_URL/$POLL_ID/votes" | jq

# ---- 7) Aggregated results ----
say "Fetching aggregated results"
curl -sS "$POLLS_URL/$POLL_ID/results" | cat
echo

# ---- 8) Show user-centric views ----
say "Listing Alice's polls"
curl -sS "$USERS_URL/$ALICE_ID/polls" | jq '.[] | {id, question, isPrivate, publishedAt, validUntil}'

say "Listing Bob's votes"
curl -sS "$USERS_URL/$BOB_ID/votes" | jq

say "Done ✅"
