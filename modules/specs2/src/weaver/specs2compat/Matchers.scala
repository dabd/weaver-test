package weaver.specs2compat

import cats.Monoid
import cats.data.Validated
import cats.effect.IO
import org.specs2.matcher.{ MatchResult, MustMatchers }
import weaver.{
  AssertionException,
  BareEffectSuite,
  Expectations,
  SourceLocation
}

trait Matchers[F[_]] extends MustMatchers {
  self: BareEffectSuite[F] =>

  implicit def toExpectations[A](
      m: MatchResult[A]
  )(
      implicit pos: SourceLocation
  ): Expectations =
    if (m.toResult.isSuccess) {
      Monoid[Expectations].empty
    } else {
      Expectations(
        Validated.invalidNel(new AssertionException(m.toResult.message, pos)))
    }

  implicit def toExpectationsF[A](
      m: MatchResult[A]
  )(
      implicit pos: SourceLocation
  ): F[Expectations] = effect.pure {
    toExpectations(m)
  }

}

trait IOMatchers extends Matchers[IO] {
  self: BareEffectSuite[IO] =>
}
