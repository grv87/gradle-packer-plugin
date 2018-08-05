package org.fidata.gradle.packer.template.helper

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.internal.TemplateObject
import org.fidata.gradle.packer.template.types.TemplateString
import org.gradle.api.tasks.Internal

import java.time.Duration

@CompileStatic
class Communicator extends TemplateObject {
  // SSH
  @JsonProperty('ssh_host')
  @Internal
  TemplateString sshHost

  @JsonProperty('ssh_port')
  @Internal
  Integer sshPort

  @JsonProperty('ssh_username')
  @Internal
  TemplateString sshUsername

  @JsonProperty('ssh_password')
  @Internal
  TemplateString sshPassword

  @JsonProperty('ssh_private_key_file')
  @Internal
  TemplateString sshPrivateKey

  @JsonProperty('ssh_pty')
  @Internal
  Boolean sshPty

  @JsonProperty('ssh_timeout')
  @Internal
  Duration sshTimeout

  @JsonProperty('ssh_agent_auth')
  @Internal
  Boolean sshAgentAuth

  @JsonProperty('ssh_disable_agent_forwarding')
  @Internal
  Boolean sshDisableAgentForwarding

  @JsonProperty('ssh_handshake_attempts')
  @Internal
  Integer sshHandshakeAttempts

  @JsonProperty('ssh_bastion_host')
  @Internal
  TemplateString sshBastionHost

  @JsonProperty('ssh_bastion_port')
  @Internal
  Integer sshBastionPort

  @JsonProperty('ssh_bastion_agent_auth')
  @Internal
  Boolean sshBastionAgentAuth

  @JsonProperty('ssh_bastion_username')
  @Internal
  TemplateString sshBastionUsername

  @JsonProperty('ssh_bastion_password')
  @Internal
  TemplateString sshBastionPassword

  @JsonProperty('ssh_bastion_private_key_file')
  @Internal
  TemplateString sshBastionPrivateKey

  @JsonProperty('ssh_file_transfer_method')
  @Internal
  TemplateString sshFileTransferMethod

  @JsonProperty('ssh_proxy_host')
  @Internal
  TemplateString sshProxyHost

  @JsonProperty('ssh_proxy_port')
  @Internal
  Integer sshProxyPort

  @JsonProperty('ssh_proxy_username')
  @Internal
  TemplateString sshProxyUsername

  @JsonProperty('ssh_proxy_password')
  @Internal
  TemplateString sshProxyPassword

  @JsonProperty('ssh_keep_alive_interval')
  @Internal
  Duration sshKeepAliveInterval

  @JsonProperty('ssh_read_write_timeout')
  @Internal
  Duration sshReadWriteTimeout

  // WinRM
  @JsonProperty('winrm_username')
  @Internal
  TemplateString winRMUser

  @JsonProperty('winrm_password')
  @Internal
  TemplateString winRMPassword

  @JsonProperty('winrm_host')
  @Internal
  TemplateString winRMHost

  @JsonProperty('winrm_port')
  @Internal
  String winRMPort

  @JsonProperty('winrm_timeout')
  @Internal
  Duration winRMTimeout

  @JsonProperty('winrm_use_ssl')
  @Internal
  Boolean winRMUseSSL

  @JsonProperty('winrm_insecure')
  @Internal
  Boolean winRMInsecure

  @JsonProperty('winrm_use_ntlm')
  @Internal
  Boolean winRMUseNTLM

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
