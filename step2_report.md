# Step 2 Report

## What Data Is Stored Locally

Using **Room**:

### UserProfile (user_profile table)
- id (Int, primary key, autogen)
- username (String) - right now unused because we have not implemented an account creating system
- score (Int)

### QuizQuestion (quiz_questions table)
- id (Int, primary key, autogen)
- questionText (String)
- correctAnswer (String)
- imageResId (Int, drawable resource ID)

---

## Persistence & Access

- **Database:** AppDatabase with entities [UserProfile, QuizQuestion]

- **DAOs:**
  - **UserDao** for UserProfile
    - insertUser() — replaces on conflict  
    - getUser() — returns single or null  
    - getUserFlow() — returns reactive Flow<UserProfile?>  
    - updateUser(user)
  - **QuestionDao** for QuizQuestion
    - getAllQuestions() — returns all questions  
    - insertQue


## Challenges

  - The live score counter on home page to always show the correct and relevant score from the game page
      For some reason that was a headache to get it working, solution was we used HomdeViewModel to observe the UserProfile and update the score dynamically

      In the HomeViewModel We created a MutableStateFlow to track the score:
      ```
        private val _score = MutableStateFlow(0)
        val score: StateFlow<Int> = _score
      ```

      We collected the UserProfile from the database using a Flow. Whenever the UserProfile updates, the score also updates.
      ```
        init {
            viewModelScope.launch {
                // Collect the Flow from the DAO for live updates
                db.userDao().getUserFlow().collect { user ->
                    _score.value = user?.score ?: 0
                }
            }
        }

      ```
      In the HomeScreen Composable We observed the score value from the HomeViewModel and displayed it in the UI using the collectAsState method:
      ```
        val score by homeViewModel.score.collectAsState()

      ```
      Then, we updated the UI with the live score in the HomeScreen:
      ```
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Score: $score",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
      ```
      This ensures that whenever the score in the database changes (such as after answering a question correctly or incorrectly in the game), the HomeScreen will automatically update with the new score.

  - Setting up the Room Database and fixing database intergrity where control codes did not match

    Solution was we had to literally go to the virtual android phone app settings and delete cache for it to creata a new database
    
