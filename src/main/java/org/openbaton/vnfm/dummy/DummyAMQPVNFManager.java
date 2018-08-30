/*
 * Copyright (c) 2016 Open Baton (http://www.openbaton.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openbaton.vnfm.dummy;

import org.openbaton.catalogue.mano.common.Event;
import org.openbaton.catalogue.mano.common.LifecycleEvent;
import org.openbaton.catalogue.mano.descriptor.VNFComponent;
import org.openbaton.catalogue.mano.descriptor.VirtualDeploymentUnit;
import org.openbaton.catalogue.mano.record.VNFCInstance;
import org.openbaton.catalogue.mano.record.VNFRecordDependency;
import org.openbaton.catalogue.mano.record.VirtualNetworkFunctionRecord;
import org.openbaton.catalogue.nfvo.Action;
import org.openbaton.catalogue.nfvo.ConfigurationParameter;
import org.openbaton.catalogue.nfvo.DependencyParameters;
import org.openbaton.catalogue.nfvo.Script;
import org.openbaton.catalogue.nfvo.viminstances.BaseVimInstance;
import org.openbaton.common.vnfm_sdk.amqp.AbstractVnfmSpringAmqp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@ConfigurationProperties
public class DummyAMQPVNFManager extends AbstractVnfmSpringAmqp {

  @Value("${vnfm.instantiate.wait:4000}")
  private int instantiateWait;

  @Value("${vnfm.instantiate.wait.random:5000}")
  private int instantiateWaitRandom;

  protected void setVnfmHelper() {}

  /**
   * This operation allows creating a VNF instance.
   *
   * @param virtualNetworkFunctionRecord
   * @param scripts
   */
  @Override
  public VirtualNetworkFunctionRecord instantiate(
      VirtualNetworkFunctionRecord virtualNetworkFunctionRecord,
      Object scripts,
      Map<String, Collection<BaseVimInstance>> vimInstances)
      throws Exception {
    log.info(
        "Instantiation of VirtualNetworkFunctionRecord " + virtualNetworkFunctionRecord.getName());

    // vnfmHelper.saveScriptOnEms(virtualNetworkFunctionRecord, scripts);

    log.debug("added parameter to config");
    log.debug("CONFIGURATION: " + virtualNetworkFunctionRecord.getConfigurations());
    ConfigurationParameter cp = new ConfigurationParameter();
    cp.setConfKey("new_key");
    cp.setValue("new_value");
    virtualNetworkFunctionRecord.getConfigurations().getConfigurationParameters().add(cp);

    Thread.sleep((int) (Math.random() * instantiateWaitRandom) + instantiateWait);

    return virtualNetworkFunctionRecord;
  }

  @Override
  public void query() {}

  @Override
  public VirtualNetworkFunctionRecord scale(
      Action scaleInOrOut,
      VirtualNetworkFunctionRecord virtualNetworkFunctionRecord,
      VNFComponent component,
      Object scripts,
      VNFRecordDependency dependency)
      throws Exception {
    log.info(
        scaleInOrOut.name()
            + " on VNFR "
            + virtualNetworkFunctionRecord.getName()
            + " with id "
            + virtualNetworkFunctionRecord.getId());
    Thread.sleep((int) (Math.random() * 500) + 1000);
    return virtualNetworkFunctionRecord;
  }

  @Override
  public void checkInstantiationFeasibility() {}

  @Override
  public VirtualNetworkFunctionRecord heal(
      VirtualNetworkFunctionRecord vnfr, VNFCInstance vnfcInstance, String s) {
    return vnfr;
  }

  @Override
  public VirtualNetworkFunctionRecord updateSoftware(
      Script script, VirtualNetworkFunctionRecord virtualNetworkFunctionRecord) throws Exception {
    log.info(
        "Update software with script "
            + script
            + " on VNFR "
            + virtualNetworkFunctionRecord.getName()
            + " with id "
            + virtualNetworkFunctionRecord.getId());
    Thread.sleep(1000 + ((int) (Math.random() * 3000)));
    return virtualNetworkFunctionRecord;
  }

  @Override
  public VirtualNetworkFunctionRecord modify(
      VirtualNetworkFunctionRecord virtualNetworkFunctionRecord, VNFRecordDependency dependency)
      throws InterruptedException {
    log.debug(
        "VirtualNetworkFunctionRecord VERSION is: " + virtualNetworkFunctionRecord.getHbVersion());
    log.debug("VirtualNetworkFunctionRecord NAME is: " + virtualNetworkFunctionRecord.getName());
    log.debug("Got dependency: " + dependency);
    log.debug("Parameters are: ");
    for (Map.Entry<String, DependencyParameters> entry : dependency.getParameters().entrySet()) {
      log.debug("Source type: " + entry.getKey());
      log.debug("Parameters: " + entry.getValue().getParameters());
    }
    Thread.sleep(3000 + ((int) (Math.random() * 7000)));
    return virtualNetworkFunctionRecord;
  }

  @Override
  public void upgradeSoftware() {}

  @Override
  public VirtualNetworkFunctionRecord terminate(
      VirtualNetworkFunctionRecord virtualNetworkFunctionRecord) {
    log.debug("RELEASE_RESOURCES");
    log.info("Releasing resources for VNFR: " + virtualNetworkFunctionRecord.getName());
    log.trace("Verison is: " + virtualNetworkFunctionRecord.getHbVersion());
    List<Event> events = new ArrayList<>();

    for (LifecycleEvent event : virtualNetworkFunctionRecord.getLifecycle_event()) {
      events.add(event.getEvent());
    }

    if (events.contains(Event.RELEASE)) {
      for (VirtualDeploymentUnit vdu : virtualNetworkFunctionRecord.getVdu())
        log.debug("Removing vdu: " + vdu);

      try {
        Thread.sleep(1000 + ((int) (Math.random() * 4000)));
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    return virtualNetworkFunctionRecord;
  }

  @Override
  public void handleError(VirtualNetworkFunctionRecord virtualNetworkFunctionRecord) {
    log.error("Error for vnfr: " + virtualNetworkFunctionRecord.getName());
  }

  @Override
  public VirtualNetworkFunctionRecord start(
      VirtualNetworkFunctionRecord virtualNetworkFunctionRecord) throws InterruptedException {
    log.debug(
        "VirtualNetworkFunctionRecord VERSION is: " + virtualNetworkFunctionRecord.getHbVersion());
    Thread.sleep((int) (Math.random() * 2000) + 2000);
    return virtualNetworkFunctionRecord;
  }

  @Override
  public VirtualNetworkFunctionRecord stop(
      VirtualNetworkFunctionRecord virtualNetworkFunctionRecord) throws Exception {
    log.info("Stop VNFR " + virtualNetworkFunctionRecord.getName());
    return virtualNetworkFunctionRecord;
  }

  @Override
  public VirtualNetworkFunctionRecord startVNFCInstance(
      VirtualNetworkFunctionRecord virtualNetworkFunctionRecord, VNFCInstance vnfcInstance)
      throws Exception {
    log.info(
        "Start VNFCInstance " + vnfcInstance.getHostname() + " with id " + vnfcInstance.getId());
    return virtualNetworkFunctionRecord;
  }

  @Override
  public VirtualNetworkFunctionRecord stopVNFCInstance(
      VirtualNetworkFunctionRecord virtualNetworkFunctionRecord, VNFCInstance vnfcInstance)
      throws Exception {
    log.info(
        "Stop VNFCInstance " + vnfcInstance.getHostname() + " with id " + vnfcInstance.getId());
    return virtualNetworkFunctionRecord;
  }

  @Override
  public VirtualNetworkFunctionRecord configure(
      VirtualNetworkFunctionRecord virtualNetworkFunctionRecord) throws InterruptedException {
    Thread.sleep((int) (Math.random() * 5000));
    return virtualNetworkFunctionRecord;
  }

  @Override
  public VirtualNetworkFunctionRecord resume(
      VirtualNetworkFunctionRecord virtualNetworkFunctionRecord,
      VNFCInstance vnfcInstance,
      VNFRecordDependency dependency)
      throws Exception {
    log.info(
        "Resume on VNFR "
            + virtualNetworkFunctionRecord.getName()
            + " with id "
            + virtualNetworkFunctionRecord.getId());
    return virtualNetworkFunctionRecord;
  }

  @Override
  public VirtualNetworkFunctionRecord executeScript(
      VirtualNetworkFunctionRecord vnfr, Script script) throws Exception {
    log.info(
        "Executing Script "
            + script.getName()
            + " on VNFR "
            + vnfr.getName()
            + " with id "
            + vnfr.getId());
    return vnfr;
  }

  public static void main(String[] args) {
    SpringApplication.run(DummyAMQPVNFManager.class, args);
  }

  @Override
  public void NotifyChange() {}
}
