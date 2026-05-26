# Social Sharing and Community Design

## 1. Purpose

Malha should be social without becoming noisy or addictive. The social layer exists to help users:

- Share finished work and progress with friends
- Reuse reliable patterns, projects, and material ideas
- Learn from real outcomes
- Invite friends into the app through useful links
- Promote their handmade work outside Malha

The app should respect user time, privacy, and creative rhythm. Social features must support crafting, not compete with crafting.

## 2. Current App Status

Implemented or partially implemented:

- Community feed screen exists
- Users can create posts with an image, description, status, and optional pattern link
- Posts support likes in the local repository flow
- Comment UI exists or is partially scaffolded
- A share icon exists on post cards, but external sharing is still TODO
- Google authentication and Firestore user profile upsert exist
- Local Room storage exists for projects, materials, patterns, progress, and posts

Not yet implemented:

- Sharing a project, pattern, or material with another user through a link
- Importing a shared project/pattern/material into the receiver's own account
- External sharing to WhatsApp, Instagram, Facebook, etc.
- Firestore sync for projects, materials, patterns, progress, and posts
- Friend-only visibility controls
- Notification-based social nudges

## 3. Core Social Principles

### 3.1 Consent First

Nothing should become public automatically.

Every shared object needs a visibility state:

- Private
- Friends only
- Unlisted link
- Public community

Default should be Private.

### 3.2 Share Useful Objects, Not Just Attention

Malha sharing should be practical. Users should be able to share:

- A finished post
- A pattern
- A project template
- A material/stash item
- A project progress snapshot
- A bundle containing pattern + materials + notes

### 3.3 Calm Social Design

Avoid:

- Infinite pressure loops
- Aggressive streak comparison
- Public shame for unfinished projects
- Engagement bait
- Pushy notifications

Prefer:

- Quiet appreciation
- Helpful comments
- Verified completions
- Optional friend circles
- Weekly summaries instead of constant alerts

## 4. Shareable App Links

Users should be able to share Malha links that open directly in the app.

Example link types:

```text
https://malha.app/pattern/{patternId}
https://malha.app/project-template/{projectId}
https://malha.app/material/{materialId}
https://malha.app/post/{postId}
https://malha.app/bundle/{bundleId}
```

If the receiver has Malha installed:

- Open the app directly
- Show a preview screen
- Allow "Save to my account"

If the receiver does not have Malha installed:

- Open a web preview page
- Show the shared content summary
- Offer install/open app CTA

## 5. Sharing Projects Between Users

### 5.1 Project Sharing Modes

Projects contain personal state, so they should not be copied blindly.

Recommended modes:

- Progress snapshot: read-only view of current progress
- Project template: copy pattern, materials list, and notes, but not personal progress
- Collaboration project: shared editable project state between selected users

### 5.2 Add Shared Project to Own Account

When user B opens user A's shared project link:

1. Show preview:
   - Project name
   - Main photo
   - Pattern used
   - Materials list
   - Difficulty
   - Estimated time
   - Notes included by owner
2. Ask what to import:
   - Pattern
   - Materials list
   - Project template
   - Notes
3. Create a new local project for user B
4. Sync to Firestore when online

User B should receive a new independent copy unless collaboration is explicitly chosen.

### 5.3 Collaboration Projects

For future versions, multiple users can edit one shared object.

Use cases:

- Friends working on a group blanket
- Teacher assigning a class project
- Parent/child shared craft project
- Pattern tester group

Required controls:

- Owner
- Editors
- Viewers
- Change history
- Restore previous version
- Last edited by

## 6. Sharing Patterns

Pattern sharing is safer than project sharing because patterns are reusable objects.

Pattern visibility:

- Private
- Friends only
- Public
- Paid/premium marketplace later

When importing a pattern:

- Preserve original creator attribution
- Create a local copy or saved reference
- Allow user notes and modifications
- Track variants/forks

Important rule:

Paid or copyrighted patterns should not be redistributed as full text unless the creator has permission to share them.

## 7. Sharing Materials

Material sharing should be lightweight and privacy-aware.

Possible uses:

- "I used this yarn"
- "I recommend this hook"
- "This stash item is available for swap"
- "This project needs these materials"

Material data that can be shared:

- Name
- Brand
- Fiber
- Weight
- Color
- Quantity recommendation
- Photo
- Purchase link, optional

Material data that should remain private by default:

- User's total stash quantity
- Purchase cost
- Storage location
- Personal notes

## 8. External Sharing to Other Apps

Users should be able to share posts and finished work to:

- WhatsApp
- Instagram
- Facebook
- Messenger
- Email
- Native Android share sheet

Use Android's standard share sheet first. It respects user choice and avoids hard dependencies on social network SDKs.

### 8.1 Share Content Format

For a finished project post:

```text
I finished "{projectName}" with Malha.

Pattern: {patternName}
Progress: 100%
Materials: {shortMaterialsSummary}

Made with Malha - a calm home for knitting and crochet.
{malhaDeepLink}
```

For a work-in-progress:

```text
Working on "{projectName}" in Malha.
Current progress: {progressPercent}%

Made with Malha - plan, stitch, finish.
{malhaDeepLink}
```

### 8.2 Promotional Reference

Every external share should include a tasteful Malha reference.

Good:

```text
Made with Malha
```

```text
Tracked in Malha
```

```text
Plan, stitch, finish with Malha
```

Avoid:

- Large forced watermarks
- Spammy referral text
- Blocking users from editing share text

### 8.3 Image Sharing

Best version:

- Generate a share card image inside the app
- Include project photo
- Project title
- Pattern name
- Small Malha logo/reference
- Optional progress badge

The user should be able to share:

- Text only
- Image only
- Image + text

## 9. Friend System

Malha does not need a heavy social graph at first.

Recommended phases:

### Phase 1: Follow/Save Creators

Users can follow creators or save profiles.

### Phase 2: Friend Circles

Users can create small private circles:

- Craft friends
- Class/group
- Family
- Pattern testers

### Phase 3: Shared Spaces

Private group spaces for:

- Challenges
- Group projects
- Pattern testing
- Local workshops

## 10. Privacy Model

Privacy must be understandable.

Each post/project/pattern should show a visibility selector:

- Private: only me
- Friends: approved friends
- Link: anyone with the link
- Public: visible in community

Users should be able to:

- Delete shared links
- Change visibility later
- Remove a post from public feed
- Block/report users
- Hide like counts optionally
- Disable external sharing of their public objects

## 11. Notifications and Social Nudges

Social notifications should be optional and quiet.

Recommended notification types:

- Friend liked your post
- Friend commented with a question
- Someone imported your public pattern
- Your shared project has an update
- Weekly community digest

Avoid daily social pressure. Daily reminders should focus on the user's own projects, not social comparison.

For privacy and wellbeing:

- Allow notification categories
- Allow quiet hours
- Allow user-selected reminder time
- Allow weekly digest instead of instant alerts
- Never notify publicly that a user has abandoned a project

## 12. Respectful Community Mechanics

Features that make Malha sociable without becoming intrusive:

- Helpful review badges
- "Made this pattern" verified completions
- Pattern correction suggestions
- Gentle appreciation reactions
- Private progress sharing with one friend
- Small group challenges
- "Craft together" session timer
- Ask Aidi to summarize comments into actionable suggestions

Avoid:

- Public leaderboards by default
- Ranking users by hours worked
- Streak shame
- Follower count pressure as a primary UI element

## 13. Trust and Safety

Required systems before large-scale public community:

- Report post
- Report comment
- Block user
- Delete own content
- Edit own content
- Content moderation queue
- Copyright report for copied patterns
- Creator attribution tracking

Community pattern trust signals:

- Verified completion
- Human reviewed
- Community validated
- Number of successful projects
- Known corrections
- Version history

## 14. Firestore and Offline Strategy

The ideal architecture is local-first plus cloud sync.

Room remains the source of truth for offline use:

- Projects
- Materials
- Patterns
- Progress
- Notes
- Draft posts

Firestore stores cloud copies:

- User profiles
- Public posts
- Shared links
- Pattern references
- Project templates
- Material recommendations
- Friend relationships

Sync rules:

- User can view and edit local data offline
- Changes are queued locally
- When online, sync worker uploads pending changes
- Firestore updates are downloaded into Room
- Conflicts show a clear "choose version" UI for important objects

Simple conflict policy for MVP:

- User-owned private objects: latest update wins
- Shared/collaborative objects: preserve both versions and ask user
- Pattern variants: create a new variant instead of overwriting

## 15. Recommended Implementation Phases

### Phase 1: External Sharing

- Implement Android share sheet for posts
- Include promotional Malha reference
- Share text + image URI when available
- Add "Copy link" placeholder for future deep links

### Phase 2: Share Links

- Add deep link routes
- Create Firestore shared link documents
- Add preview screen
- Allow import pattern/project/material into local Room

### Phase 3: Cloud Sync

- Add Firestore repositories for projects/materials/patterns
- Add pending sync queue
- Add WorkManager sync
- Add offline conflict handling

### Phase 4: Friends and Privacy

- Add friend requests
- Add visibility selector
- Add friends-only feed
- Add block/report basics

### Phase 5: Collaborative Projects

- Shared editable project state
- Role permissions
- Change history
- Activity log

## 16. Best Next App Improvements

Highest value next steps:

1. Implement external sharing for community posts through Android share sheet.
2. Add visibility controls to posts: private, friends, public.
3. Add "Save pattern from post" and "Start project from post".
4. Add shareable deep links for public posts and patterns.
5. Add cloud sync for projects and materials while keeping Room offline-first.
6. Add notification preferences with user-selected daily project reminder time.
7. Unify account profile editing so username, bio, and profile photo live in one account screen.

## 17. Product Positioning

Malha's social identity should be:

> A calm, useful craft community where finished work, reliable patterns, and helpful notes travel between makers.

Not:

> Another noisy social feed.

The strongest social promise is:

> Share what you made. Save what helps. Keep your craft life yours.
