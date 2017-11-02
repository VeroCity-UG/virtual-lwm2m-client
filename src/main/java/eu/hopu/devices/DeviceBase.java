package eu.hopu.devices;

import eu.hopu.dto.Battery;
import eu.hopu.objects.DeviceObject;
import org.eclipse.leshan.client.californium.LeshanClient;
import org.eclipse.leshan.client.californium.LeshanClientBuilder;
import org.eclipse.leshan.client.object.Server;
import org.eclipse.leshan.client.resource.LwM2mObjectEnabler;
import org.eclipse.leshan.client.resource.ObjectsInitializer;
import org.eclipse.leshan.core.model.LwM2mModel;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.request.BindingMode;

import java.util.List;

import static org.eclipse.leshan.LwM2mId.DEVICE;
import static org.eclipse.leshan.LwM2mId.SECURITY;
import static org.eclipse.leshan.LwM2mId.SERVER;
import static org.eclipse.leshan.client.object.Security.noSec;

public abstract class DeviceBase {

    private String name;
    private String serverUrl;
    private String serverPort;
    private int lifetime;
    private Battery device;
    private String localAddress;
    private int localPort;

    public DeviceBase() {
    }

    public DeviceBase(String name, String serverUrl, String serverPort, int lifetime, Battery device, String localAddress, int localPort) {
        this.name = name;
        this.serverUrl = serverUrl;
        this.serverPort = serverPort;
        this.lifetime = lifetime;
        this.device = device;
        this.localAddress = localAddress;
        this.localPort = localPort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public int getLifetime() {
        return lifetime;
    }

    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }

    public Battery getDevice() {
        return device;
    }

    public void setDevice(Battery device) {
        this.device = device;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    public int getLocalPort() {
        return localPort;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    public LeshanClient getLeshanClient(List<ObjectModel> models) {

        ObjectsInitializer objectsInitializer = getObjectInitializer(models);
        List<LwM2mObjectEnabler> enablers = getDeviceEnabledObjects(objectsInitializer);

        LeshanClientBuilder builder = new LeshanClientBuilder(getName());
        builder.setLocalAddress(getLocalAddress(), getLocalPort());
        builder.setObjects(enablers);

        return builder.build();
    }

    public ObjectsInitializer getObjectInitializer(List<ObjectModel> models) {
        ObjectsInitializer initializer = new ObjectsInitializer(new LwM2mModel(models));

        initializer.setInstancesForObject(SECURITY, noSec(getServerUrl() + ":" + getServerPort(), 123));
        initializer.setInstancesForObject(SERVER, new Server(123, getLifetime(), BindingMode.U, false));

        Battery deviceDto = getDevice();
        if (deviceDto != null)
            initializer.setInstancesForObject(DEVICE, new DeviceObject(deviceDto.getBatteryStatus(), deviceDto.getBatteryLevel()));

        return initializer;
    }

    abstract List<LwM2mObjectEnabler> getDeviceEnabledObjects(ObjectsInitializer objectsInitializer);
}