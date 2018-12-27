package org.fidata.ovf

import static org.apache.commons.io.FilenameUtils.getExtension
import com.google.inject.Injector
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.gradle.internal.impldep.org.apache.commons.io.FilenameUtils
import org.jclouds.ContextBuilder
import org.jclouds.cim.ResourceAllocationSettingData
import org.jclouds.compute.stub.StubApiMetadata
import org.jclouds.http.functions.ParseSax
import org.jclouds.ovf.Envelope
import org.jclouds.ovf.VirtualHardwareSection

final class OvfUtils {
  public static final String OVF_FILE_EXTENSION = 'ovf'
  public static final String OVA_FILE_EXTENSION = 'ova'

  static Integer getCpusFromOvf(File ovfFile) {
    ovfFile.withInputStream { InputStream inputStream ->
      Injector injector = new ContextBuilder(new StubApiMetadata()).buildInjector()
      Set<VirtualHardwareSection> virtualHardwareSections =
        injector.getInstance(ParseSax.Factory).create(injector.getInstance(/*ParseSax.HandlerWithResult<Envelope>*/EnvelopeHandler))
          .parse(inputStream).virtualSystem.virtualHardwareSections
      if (virtualHardwareSections.size() != 1) {
        // TODO: log warning
        return null
      }
      List<ResourceAllocationSettingData> processorData = virtualHardwareSections[0].items.findAll { ResourceAllocationSettingData resourceAllocationSettingData ->
        resourceAllocationSettingData.resourceType == ResourceAllocationSettingData.ResourceType.PROCESSOR
      }
      if (processorData.size() != 1) {
        // TODO: log warning
        return null
      }
      if (processorData[0].virtualQuantityUnits != null && processorData[0].virtualQuantityUnits != 'count') {
        // TODO: log warning
        return null
      }
      processorData[0].virtualQuantity.toInteger() // TODO
    }
  }

  static Integer getCpusFromOvfOrOva(File ofvOrOvaFile) {
    Integer result = null
    switch (getExtension(ofvOrOvaFile.toString())) {
      case null:
        break
      case OVF_FILE_EXTENSION:
        result = getCpusUsedFromOvf(ofvOrOvaFile)
        break
      case 'ova':
        ofvOrOvaFile.withInputStream { InputStream ovaInputStream ->
          new GzipCompressorInputStream(ovaInputStream).withStream { InputStream gzInputStream ->
            new TarArchiveInputStream(gzInputStream).withStream { TarArchiveInputStream tarInputStream ->
              TarArchiveEntry tarEntry
              while ((tarEntry = tarInputStream.nextEntry) != null) {
                if (FilenameUtils.getExtension(tarEntry.name) == OVF_FILE_EXTENSION) {
                  if (originalCpus == null) {
                    originalCpus = getCpusUsedFromOvf(tarEntry.file)
                  } else {
                    // TODO: log warning
                  }
                }
              }
            }
          }
        }
        break
      default:
        null
    }
    result
  }

  private OvfUtils() {}
}
