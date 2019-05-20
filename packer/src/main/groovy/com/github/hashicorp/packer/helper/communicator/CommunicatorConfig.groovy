/*
 * CommunicatorConfig class
 * Copyright ©  Basil Peace
 *
 * This file is part of gradle-packer-plugin.
 *
 * This plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this plugin.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Ported from original Packer code,
 * file helper/communicator/config.go
 * under the terms of the Mozilla Public License, v. 2.0.
 */
package com.github.hashicorp.packer.helper.communicator

import com.fasterxml.jackson.annotation.JsonAlias
import groovy.transform.CompileStatic
import org.fidata.packer.engine.annotations.Timing
import org.fidata.packer.engine.annotations.ConnectionSetting
import org.fidata.packer.engine.annotations.Credential
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableString
import org.fidata.packer.engine.types.InterpolableInteger
import org.fidata.packer.engine.types.InterpolableBoolean
import org.fidata.packer.engine.types.InterpolableDuration

@CompileStatic
// TODO: communicator/config ?
abstract class CommunicatorConfig implements InterpolableObject<CommunicatorConfig> {
  // SSH
  @ConnectionSetting
  abstract InterpolableString getSshHost()

  @ConnectionSetting
  abstract InterpolableInteger getSshPort()

  @Credential
  abstract InterpolableString getSshUsername()

  @Credential
  abstract InterpolableString getSshPassword()

  @Credential
  abstract InterpolableString getSshPrivateKey()

  @ConnectionSetting
  abstract InterpolableBoolean getSshPty()

  @JsonAlias('ssh_wait_timeout')
  @Timing
  abstract InterpolableDuration getSshTimeout()

  @ConnectionSetting
  abstract InterpolableBoolean getSshAgentAuth()

  @ConnectionSetting
  abstract InterpolableBoolean getSshDisableAgentForwarding()

  @Timing
  abstract InterpolableInteger getSshHandshakeAttempts()

  @ConnectionSetting
  abstract InterpolableString getSshBastionHost()

  @ConnectionSetting
  abstract InterpolableInteger getSshBastionPort()

  @ConnectionSetting
  abstract InterpolableBoolean getSshBastionAgentAuth()

  @Credential
  abstract InterpolableString getSshBastionUsername()

  @Credential
  abstract InterpolableString getSshBastionPassword()

  @Credential
  abstract InterpolableString getSshBastionPrivateKey()

  @ConnectionSetting
  abstract InterpolableString getSshFileTransferMethod()

  @ConnectionSetting
  abstract InterpolableString getSshProxyHost()

  @ConnectionSetting
  abstract InterpolableInteger getSshProxyPort()

  @Credential
  abstract InterpolableString getSshProxyUsername()

  @Credential
  abstract InterpolableString getSshProxyPassword()

  @Timing
  abstract InterpolableDuration getSshKeepAliveInterval()

  @Timing
  abstract InterpolableDuration getSshReadWriteTimeout()

  // winrm
  @Credential
  abstract InterpolableString getWinrmUser()

  @Credential
  abstract InterpolableString getWinrmPassword()

  @ConnectionSetting
  abstract InterpolableString getWinrmHost()

  @ConnectionSetting
  abstract InterpolableInteger getWinrmPort()

  @Timing
  abstract InterpolableDuration getWinrmTimeout()

  @ConnectionSetting
  abstract InterpolableBoolean getWinrmUseSSL()

  @ConnectionSetting
  abstract InterpolableBoolean getWinrmInsecure()

  @ConnectionSetting
  abstract InterpolableBoolean getWinrmUseNTLM()
}
