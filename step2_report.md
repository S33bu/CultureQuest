#Step 2 report

#What data is stored locally
Using Room: 
- User Profile:
  - id (Int)
  - username (String)
  - score (Int)

-QuizQuestion (quizquestion table)
  - id (Int)
  - questionText (String)
  - correctAnswer (String)
  - imageResId (Int)

Persistence & Access
- Database AppDatabase with entities = UserProfile, QuizQuestion

- DAO UserDao for UserProfile 
  - insertUser(REPLACE on conflict)
  - getUser() (single, nullable)
  - getUserFlow() (reactive Flow<UserProfile?>)
  - updateUser(user)
  
- QuestionDao for QuizQuestion
  - getAllQuestions() (list)
  - insertQuestion(question) (REPLACE)
  - insertAll(questions) (REPLACE)
  - clearAll()

#Basic form & validation (implementation note)
Validation (e.g. user must enter text before saving) is enforced in the UI layer
before calling DAO methods

#Challenges and solutions