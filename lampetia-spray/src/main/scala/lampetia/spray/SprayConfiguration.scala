package lampetia.spray

import lampetia.conf.Configuration

/**
 * @author Hossam Karim
 */

trait SprayConfiguration {

  def configuration: Configuration

  def moduleConfigurationPrefix: String

  def apiPrefix: String =
    configuration.config.getString(s"$moduleConfigurationPrefix.spray.api.prefix")

  def serviceHost: String =
    configuration.config.getString(s"$moduleConfigurationPrefix.spray.service.host")

  def servicePort: Int =
    configuration.config.getInt(s"$moduleConfigurationPrefix.spray.service.port")


}