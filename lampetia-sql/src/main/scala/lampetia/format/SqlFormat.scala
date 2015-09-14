package lampetia.format

import lampetia.model._
import lampetia.sql.SqlCodec

/**
 * @author Hossam Karim
 */

trait SqlFormat {

  val dialect: SqlCodec
  import dialect._

  implicit lazy val consumeUserId: Consume[UserId] = consume[String].fmap(UserId)
  implicit lazy val produceUserId: Produce[UserId] = a => produce(a.value)

  implicit lazy val consumeName: Consume[Name] = consume[String].fmap(Name)
  implicit lazy val produceName: Produce[Name] = a => produce(a.value)

  implicit lazy val consumeTitle: Consume[Title] = consume[String].fmap(Title)
  implicit lazy val produceTitle: Produce[Title] = a => produce(a.value)

  implicit lazy val consumeUrl: Consume[Url] = consume[String].fmap(Url)
  implicit lazy val consumeUrlOption: Consume[Option[Url]] = consume[Option[String]].fmap(_.map(Url))
  implicit lazy val produceUrl: Produce[Url] = a => produce(a.value)
  implicit lazy val produceUrlOption: Produce[Option[Url]] = a => produce(a.map(_.value))

  implicit lazy val consumeLocale: Consume[Locale] = consume[String].fmap(Locale)
  implicit lazy val produceLocale: Produce[Locale] = a => produce(a.value)

  implicit lazy val consumePhone: Consume[Phone] = consume[String].fmap(Phone)

  implicit lazy val producePhone: Produce[Phone] = a => produce(a.value)


}
