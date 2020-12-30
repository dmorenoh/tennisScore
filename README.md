# tennisScore
## Implementation
Tennis kata was solver by using Kotlin and a bit of Arrow (as per functional programming approaches).
## Feature 1
All suggested acceptance criteria were considered on order to cover this part. Those are depicted on
`BasicTennisScoreTest`. 
On the other hand, additional constraints were detected when addressing the most ideal solution:
- No more scores can be reported once `TennisScore` has already a winner
- Returning formatted score should not involve any additional change over the current `TennisScore` state.
- Considering this, winner detection is performed when adding score to either player. By considering [CQS]
  (https://martinfowler.com/bliki/CommandQuerySeparation.html) principle

As a summary, the main idea is represented in two parts:
### Player scores command:
The workflow is very simple:
1. Check if there is a winner
2. Add score to a given player (either SERVER/RECEIVER) 
3. Tries to detect if after that there is a winner, If so, `TennisScore` is set with that winner player.
```kotlin
fun BasicTennisScore.playerScores(playerType: PlayerType): Either<DomainError, BasicTennisScore> =
    if (winner != EmptyPlayer) Either.left(DomainError("There is a winner already"))
    else Either.right(this.addScoreTo(playerType).calculateWinner())
```
### Return score query
The same, just returns the score as formatted not changing object status.
```kotlin
    fun printScore(): String = when {
    winner is TennisPlayer -> printWinner(winner)
    matchIsEven() -> printScoreAsEven()
    else -> this.printScoreDefault()
}
```
As you can see, immutability was considered in all aspects. Every change implies a new object creation, this is 
being re-enforced by following functional paradigms. 

## Feature 2
It is just allowed configure minimum difference between opponents when detecting a winner. This can be achieved when creating a new brand `BasicTennisScore` object and setting the 
`minToWin` field, for example:
```kotlin
 BasicTennisScore(server = server, receiver = receiver, winner = server, minToWin = 0)
```
Additionally, commands over `BasicTennisScore` can be extended by adding a new extension function over the previous 
result. For example, let's imagine we need to restart the score when reaches to "40:40". For that we might create a 
custom (extension) function as `restartGameIfRequired()` which check if this is true and return that restarted score 
or return the old one otherwise.   
```kotlin
var finalResult = anyTennisScore.playerScores(SERVER).restartGameIfRequired()
```
This new function was not covered thought. 


