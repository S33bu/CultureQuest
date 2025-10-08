# Step 2 Report

## What Data Is Stored Locally

Using **Room**:

### UserProfile (user_profile table)
- id (Int, primary key, autogen)
- username (String)
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
