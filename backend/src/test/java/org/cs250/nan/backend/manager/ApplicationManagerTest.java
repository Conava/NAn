package org.cs250.nan.backend.manager;

import org.cs250.nan.backend.config.AppProperties;
import org.cs250.nan.backend.config.SettingsService;
import org.cs250.nan.backend.service.JsonWriterService;
import org.cs250.nan.backend.service.MonitoringManager;
import org.cs250.nan.backend.service.SingleScanService;
import org.json.JSONObject;
import org.springframework.context.ConfigurableApplicationContext;

import org.junit.jupiter.api.*;
//import org.mockito.Mockito;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;
public class ApplicationManagerTest {
    private MonitoringManager monitoringManager;
    private SingleScanService singleScanService;
    private JsonWriterService jsonWriterService;
    private AppProperties props;
    private SettingsService settingsService;
    private ConfigurableApplicationContext context;
    private ApplicationManager appManager;
    @BeforeAll
    static void initial() {
        System.out.println("Initializing tests...");
    }
    @BeforeEach
    void setUp() {
        monitoringManager = mock(MonitoringManager.class);
        singleScanService = mock(SingleScanService.class);
        jsonWriterService = mock(JsonWriterService.class);
        props = new AppProperties();  // You can mock if needed
        settingsService = mock(SettingsService.class);
        context = mock(ConfigurableApplicationContext.class);

        appManager = new ApplicationManager(monitoringManager, singleScanService, jsonWriterService,
                props, settingsService, context);
    }
    @AfterAll
    static void cleanup(){
        System.out.println("Tests completed...");
    }
    @Test
    void testStartMonitoring() {
        String result = appManager.startMonitoring();
        verify(monitoringManager).startMonitoring();
        assertEquals("Monitoring started.", result);
    }

    @Test
    void testStopMonitoring() {
        String result = appManager.stopMonitoring();
        verify(monitoringManager).stopMonitoring();
        assertEquals("Monitoring stopped.", result);
    }

    @Test
    void testShutdownApplication() {
        doNothing().when(monitoringManager).stopMonitoring();
        String result = appManager.shutdownApplication();
        verify(monitoringManager).stopMonitoring();
        assertTrue(result.contains("Shutdown initiated"));
    }

    @Test
    void testUpdateSettings_Success() throws IOException {
        when(monitoringManager.isMonitoringActive()).thenReturn(true);

        AppProperties newProps = new AppProperties();
        newProps.setBaseDir("base");
        newProps.setDataStorage("json");

        String result = appManager.updateSettings(newProps);

        verify(monitoringManager).stopMonitoring();
        verify(settingsService).saveExternalConfig(any());
        verify(monitoringManager).startMonitoring();
        assertTrue(result.startsWith("All settings updated"));
    }

    @Test
    void testUpdateSettings_IOExceptionHandled() throws IOException {
        when(monitoringManager.isMonitoringActive()).thenReturn(true);
        doThrow(new IOException("Disk error")).when(settingsService).saveExternalConfig(any());

        AppProperties newProps = new AppProperties();
        String result = appManager.updateSettings(newProps);

        assertTrue(result.contains("WARNING: settings could not be saved"));
    }

    @Test
    void testRunSingleScan_SuccessfulWithPath() {
        var cfg = new AppProperties.Monitor();
        List<JSONObject> mockResults = List.of(new JSONObject());

        when(singleScanService.run(any())).thenReturn(CompletableFuture.completedFuture(mockResults));
        when(jsonWriterService.writeJsonFile(any(), any())).thenReturn("output.json");

        appManager.runSingleScan(cfg).thenAccept(result -> {
            assertTrue(result.contains("returned 1 result"));
            assertTrue(result.contains("output.json"));
        }).join();
    }

    @Test
    void testRunSingleScan_NullResult() {
        when(singleScanService.run(any())).thenReturn(CompletableFuture.completedFuture(null));
        when(jsonWriterService.writeJsonFile(any(), any())).thenReturn("path.json");

        appManager.runSingleScan(null).thenAccept(result -> {
            assertTrue(result.contains("returned 0 result"));
        }).join();
    }

    @Test
    void testRunSingleScan_FileWriteFails() {
        List<JSONObject> results = List.of(new JSONObject());

        when(singleScanService.run(any())).thenReturn(CompletableFuture.completedFuture(results));
        when(jsonWriterService.writeJsonFile(any(), any())).thenReturn(null);

        appManager.runSingleScan(null).thenAccept(result -> {
            assertTrue(result.contains("Failed to save JSON"));
        }).join();
    }

    @Test
    void testExportMonitoringData_WithData() {
        var data = List.of(new JSONObject());
        when(monitoringManager.getMonitoringData()).thenReturn(data);
        when(jsonWriterService.writeJsonFile(any(), any())).thenReturn("data.json");

        String result = appManager.exportMonitoringData("data.json");
        assertEquals("Data exported to data.json", result);
    }

    @Test
    void testExportMonitoringData_Empty() {
        when(monitoringManager.getMonitoringData()).thenReturn(Collections.emptyList());

        String result = appManager.exportMonitoringData("data.json");
        assertEquals("No monitoring data to export.", result);
    }

    @Test
    void testGetMonitoringData() {
        var data = List.of(new JSONObject());
        when(monitoringManager.getMonitoringData()).thenReturn(data);

        List<JSONObject> result = appManager.getMonitoringData();
        assertEquals(data, result);
    }

    @Test
    void testGetApplicationState() {
        when(monitoringManager.isMonitoringActive()).thenReturn(true);

        var state = appManager.getApplicationState();
        assertTrue(state.monitoring());
        assertEquals(0, state.lastScanResultCount());
    }
}
