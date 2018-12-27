package com.github.hashicorp.packer.helper

import com.fasterxml.jackson.annotation.JsonAlias
import groovy.transform.CompileStatic
import org.fidata.packer.engine.types.base.InterpolableObject
import org.gradle.api.tasks.Internal
import org.fidata.packer.engine.types.InterpolableString
import org.fidata.packer.engine.types.InterpolableInteger
import org.fidata.packer.engine.types.InterpolableBoolean
import org.fidata.packer.engine.types.InterpolableDuration

@CompileStatic
// TODO: communicator/config ?
class Communicator extends InterpolableObject {
  // SSH
  @Internal
  InterpolableString sshHost

  @Internal
  InterpolableInteger sshPort

  @Internal
  InterpolableString sshUsername

  @Internal
  InterpolableString sshPassword

  @Internal
  InterpolableString sshPrivateKey

  @Internal
  InterpolableBoolean sshPty

  @JsonAlias('ssh_wait_timeout')
  @Internal
  InterpolableDuration sshTimeout

  @Internal
  InterpolableBoolean sshAgentAuth

  @Internal
  InterpolableBoolean sshDisableAgentForwarding

  @Internal
  InterpolableInteger sshHandshakeAttempts

  @Internal
  InterpolableString sshBastionHost

  @Internal
  InterpolableInteger sshBastionPort

  @Internal
  InterpolableBoolean sshBastionAgentAuth

  @Internal
  InterpolableString sshBastionUsername

  @Internal
  InterpolableString sshBastionPassword

  @Internal
  InterpolableString sshBastionPrivateKey

  @Internal
  InterpolableString sshFileTransferMethod

  @Internal
  InterpolableString sshProxyHost

  @Internal
  InterpolableInteger sshProxyPort

  @Internal
  InterpolableString sshProxyUsername

  @Internal
  InterpolableString sshProxyPassword

  @Internal
  InterpolableDuration sshKeepAliveInterval

  @Internal
  InterpolableDuration sshReadWriteTimeout

  // winrm
  @Internal
  InterpolableString winrmUser

  @Internal
  InterpolableString winrmPassword

  @Internal
  InterpolableString winrmHost

  @Internal
  InterpolableInteger winrmPort

  @Internal
  InterpolableDuration winrmTimeout

  @Internal
  InterpolableBoolean winrmUseSSL

  @Internal
  InterpolableBoolean winrmInsecure

  @Internal
  InterpolableBoolean winrmUseNTLM

  @Override
  protected void doInterpolate() {
    sshHost.interpolate context
    sshPort.interpolate context
    sshUsername.interpolate context
    sshPassword.interpolate context
    sshPrivateKey.interpolate context
    sshPty.interpolate context
    sshTimeout.interpolate context
    sshAgentAuth.interpolate context
    sshDisableAgentForwarding.interpolate context
    sshHandshakeAttempts.interpolate context
    sshBastionHost.interpolate context
    sshBastionPort.interpolate context
    sshBastionAgentAuth.interpolate context
    sshBastionHost.interpolate context
    sshBastionUsername.interpolate context
    sshBastionPassword.interpolate context
    sshBastionPrivateKey.interpolate context
    sshFileTransferMethod.interpolate context
    sshProxyHost.interpolate context
    sshProxyPort.interpolate context
    sshProxyUsername.interpolate context
    sshProxyPassword.interpolate context
    sshKeepAliveInterval.interpolate context
    sshReadWriteTimeout.interpolate context
    winrmUser.interpolate context
    winrmPassword.interpolate context
    winrmHost.interpolate context
    winrmPort.interpolate context
    winrmTimeout.interpolate context
    winrmUseSSL.interpolate context
    winrmInsecure.interpolate context
    winrmUseNTLM.interpolate context
  }
}
