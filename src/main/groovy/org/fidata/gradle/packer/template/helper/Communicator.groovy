package org.fidata.gradle.packer.template.helper

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.internal.InterpolableObject
import org.fidata.gradle.packer.template.types.InterpolableBoolean
import org.fidata.gradle.packer.template.types.InterpolableDuration
import org.fidata.gradle.packer.template.types.InterpolableInteger
import org.fidata.gradle.packer.template.types.InterpolableString
import org.gradle.api.tasks.Internal

@CompileStatic
class Communicator extends InterpolableObject {
  // SSH
  @JsonProperty('ssh_host')
  @Internal
  InterpolableString sshHost

  @JsonProperty('ssh_port')
  @Internal
  InterpolableInteger sshPort

  @JsonProperty('ssh_username')
  @Internal
  InterpolableString sshUsername

  @JsonProperty('ssh_password')
  @Internal
  InterpolableString sshPassword

  @JsonProperty('ssh_private_key_file')
  @Internal
  InterpolableString sshPrivateKey

  @JsonProperty('ssh_pty')
  @Internal
  InterpolableBoolean sshPty

  @JsonProperty('ssh_timeout')
  @Internal
  InterpolableDuration sshTimeout

  @JsonProperty('ssh_agent_auth')
  @Internal
  InterpolableBoolean sshAgentAuth

  @JsonProperty('ssh_disable_agent_forwarding')
  @Internal
  InterpolableBoolean sshDisableAgentForwarding

  @JsonProperty('ssh_handshake_attempts')
  @Internal
  InterpolableInteger sshHandshakeAttempts

  @JsonProperty('ssh_bastion_host')
  @Internal
  InterpolableString sshBastionHost

  @JsonProperty('ssh_bastion_port')
  @Internal
  InterpolableInteger sshBastionPort

  @JsonProperty('ssh_bastion_agent_auth')
  @Internal
  InterpolableBoolean sshBastionAgentAuth

  @JsonProperty('ssh_bastion_username')
  @Internal
  InterpolableString sshBastionUsername

  @JsonProperty('ssh_bastion_password')
  @Internal
  InterpolableString sshBastionPassword

  @JsonProperty('ssh_bastion_private_key_file')
  @Internal
  InterpolableString sshBastionPrivateKey

  @JsonProperty('ssh_file_transfer_method')
  @Internal
  InterpolableString sshFileTransferMethod

  @JsonProperty('ssh_proxy_host')
  @Internal
  InterpolableString sshProxyHost

  @JsonProperty('ssh_proxy_port')
  @Internal
  InterpolableInteger sshProxyPort

  @JsonProperty('ssh_proxy_username')
  @Internal
  InterpolableString sshProxyUsername

  @JsonProperty('ssh_proxy_password')
  @Internal
  InterpolableString sshProxyPassword

  @JsonProperty('ssh_keep_alive_interval')
  @Internal
  InterpolableDuration sshKeepAliveInterval

  @JsonProperty('ssh_read_write_timeout')
  @Internal
  InterpolableDuration sshReadWriteTimeout

  // WinRM
  @JsonProperty('winrm_username')
  @Internal
  InterpolableString winRMUser

  @JsonProperty('winrm_password')
  @Internal
  InterpolableString winRMPassword

  @JsonProperty('winrm_host')
  @Internal
  InterpolableString winRMHost

  @JsonProperty('winrm_port')
  @Internal
  InterpolableInteger winRMPort

  @JsonProperty('winrm_timeout')
  @Internal
  InterpolableDuration winRMTimeout

  @JsonProperty('winrm_use_ssl')
  @Internal
  InterpolableBoolean winRMUseSSL

  @JsonProperty('winrm_insecure')
  @Internal
  InterpolableBoolean winRMInsecure

  @JsonProperty('winrm_use_ntlm')
  @Internal
  InterpolableBoolean winRMUseNTLM

  @Override
  protected void doInterpolate(Context ctx) {
    sshHost.interpolate ctx
    sshUsername.interpolate ctx
    sshPassword.interpolate ctx
    sshPrivateKey.interpolate ctx
    sshBastionHost.interpolate ctx
    sshBastionUsername.interpolate ctx
    sshBastionPassword.interpolate ctx
    sshBastionPrivateKey.interpolate ctx
    sshFileTransferMethod.interpolate ctx
    sshProxyHost.interpolate ctx
    sshProxyUsername.interpolate ctx
    sshProxyPassword.interpolate ctx
    winRMUser.interpolate ctx
    winRMPassword.interpolate ctx
    winRMHost.interpolate ctx
  }
}
