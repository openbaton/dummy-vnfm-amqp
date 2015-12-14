package org.openbaton.vnfm.dummy;

import org.openbaton.catalogue.mano.common.Event;
import org.openbaton.catalogue.mano.common.LifecycleEvent;
import org.openbaton.catalogue.mano.descriptor.VirtualDeploymentUnit;
import org.openbaton.catalogue.mano.record.VNFCInstance;
import org.openbaton.catalogue.mano.record.VNFRecordDependency;
import org.openbaton.catalogue.mano.record.VirtualNetworkFunctionRecord;
import org.openbaton.catalogue.nfvo.Action;
import org.openbaton.catalogue.nfvo.ConfigurationParameter;
import org.openbaton.catalogue.nfvo.DependencyParameters;
import org.openbaton.common.vnfm_sdk.amqp.AbstractVnfmSpringAmqp;
import org.springframework.boot.SpringApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lto on 27/05/15.
 */

public class DummyAMQPVNFManager extends AbstractVnfmSpringAmqp {


    /**
     * This operation allows creating a VNF instance.
     *
     * @param virtualNetworkFunctionRecord
     * @param scripts
     */
    @Override
    public VirtualNetworkFunctionRecord instantiate(VirtualNetworkFunctionRecord virtualNetworkFunctionRecord, Object scripts) throws Exception {
        log.info("Instantiation of VirtualNetworkFunctionRecord " + virtualNetworkFunctionRecord.getName());

        // vnfmHelper.saveScriptOnEms(virtualNetworkFunctionRecord, scripts);

        log.debug("added parameter to config");
        log.debug("CONFIGURATION: " + virtualNetworkFunctionRecord.getConfigurations());
        ConfigurationParameter cp = new ConfigurationParameter();
        cp.setConfKey("new_key");
        cp.setValue("new_value");
        virtualNetworkFunctionRecord.getConfigurations().getConfigurationParameters().add(cp);

        Thread.sleep((int) (Math.random() * 5000) + 4000);

        return virtualNetworkFunctionRecord;
    }


    protected void setVnfmHelper() {

    }

    @Override
    public void query() {

    }

    @Override
    public VirtualNetworkFunctionRecord scale(Action scaleInOrOut, VirtualNetworkFunctionRecord virtualNetworkFunctionRecord, VNFCInstance component, Object scripts, VNFRecordDependency dependency) throws Exception {
        return virtualNetworkFunctionRecord;
    }


    @Override
    public void checkInstantiationFeasibility() {

    }

    @Override
    public void heal() {

    }

    @Override
    public void updateSoftware() {

    }

    @Override
    public VirtualNetworkFunctionRecord modify(VirtualNetworkFunctionRecord virtualNetworkFunctionRecord, VNFRecordDependency dependency) throws InterruptedException {
        log.debug("VirtualNetworkFunctionRecord VERSION is: " + virtualNetworkFunctionRecord.getHb_version());
        log.debug("VirtualNetworkFunctionRecord NAME is: " + virtualNetworkFunctionRecord.getName());
        log.debug("Got dependency: " + dependency);
        log.debug("Parameters are: ");
        for (Map.Entry<String, DependencyParameters> entry : dependency.getParameters().entrySet()){
            log.debug("Source type: " + entry.getKey());
            log.debug("Parameters: " + entry.getValue().getParameters());
        }
        Thread.sleep(3000 + ((int) (Math.random()*7000)));
        return virtualNetworkFunctionRecord;
    }


    @Override
    public void upgradeSoftware() {

    }

    @Override
    public VirtualNetworkFunctionRecord terminate(VirtualNetworkFunctionRecord virtualNetworkFunctionRecord) {
        log.debug("RELEASE_RESOURCES");
        log.info("Releasing resources for VNFR: " + virtualNetworkFunctionRecord.getName());
        log.trace("Verison is: " + virtualNetworkFunctionRecord.getHb_version());
        List<Event> events = new ArrayList<>();

        for (LifecycleEvent event : virtualNetworkFunctionRecord.getLifecycle_event()){
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
    public VirtualNetworkFunctionRecord start(VirtualNetworkFunctionRecord virtualNetworkFunctionRecord) throws InterruptedException {
        log.debug("VirtualNetworkFunctionRecord VERSION is: " + virtualNetworkFunctionRecord.getHb_version());
        Thread.sleep((int) (Math.random() * 2000) + 2000);
        return virtualNetworkFunctionRecord;
    }

    @Override
    public VirtualNetworkFunctionRecord configure(VirtualNetworkFunctionRecord virtualNetworkFunctionRecord) throws InterruptedException {
        Thread.sleep((int) (Math.random() * 5000));
        return virtualNetworkFunctionRecord;
    }

    public static void main(String[] args) {
        SpringApplication.run(DummyAMQPVNFManager.class, args);
    }

    @Override
    public void NotifyChange() {

    }

    @Override
    protected void checkEmsStarted(String hostname) {

    }
}
