package org.example

class Utils implements Serializable {
    def steps

    Utils(steps) {
        this.steps = steps
    }

    def loadConfig(String path) {
        def props = steps.readProperties(file: path)
        return props
    }
}

