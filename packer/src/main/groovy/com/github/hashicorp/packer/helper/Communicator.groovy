package com.github.hashicorp.packer.helper

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
abstract class Communicator implements InterpolableObject<Communicator> {
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
