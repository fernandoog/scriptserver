package com.fernandoog.controller;

import com.fernandoog.exception.ResourceNotFoundException;
import com.fernandoog.model.Script;
import com.fernandoog.repository.ScriptRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/")
public class ScriptController {

    public static final String EXITED_WITH_ERROR_CODE = "Exited with error code : ";
    Logger log = LoggerFactory.getLogger(ScriptController.class);

    @Autowired
    private ScriptRepository scriptRepository;

    // create script rest api
    @PostMapping("/scripts")
    public Script createScript(@RequestBody Script script) {
        return scriptRepository.save(script);
    }

    // delete script rest api
    @DeleteMapping("/scripts/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteScript(@PathVariable Long id) {
        Script script = scriptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Script not exist with id :" + id));

        scriptRepository.delete(script);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }

    // get all scripts
    @GetMapping("/scripts")
    public List<Script> getAllScripts() {
        return scriptRepository.findAll();
    }

    // get script by id rest api
    @GetMapping("/scripts/{id}")
    public ResponseEntity<Script> getScriptById(@PathVariable Long id) {
        Script script = scriptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Script not exist with id :" + id));
        return ResponseEntity.ok(script);
    }

    // update script rest api
    @PutMapping("/scripts/{id}")
    public ResponseEntity<Script> updateScript(@PathVariable Long id, @RequestBody Script scriptDetails) {
        Script script = scriptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Script not exist with id :" + id));

        script.setCode(scriptDetails.getCode());

        Script updatedScript = scriptRepository.save(script);
        return ResponseEntity.ok(updatedScript);
    }

    // Execute script by id rest api
    @GetMapping("/scripts/exec/{id}")
    public ResponseEntity<Script> launchScriptById(@PathVariable Long id) {

        Script script = scriptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Script not exist with id :" + id));

        try {
            script.setResult(String.valueOf(Runtime.getRuntime().exec(script.getCode())));
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return ResponseEntity.ok(script);
    }

}
