package org.example

class Utils implements Serializable {
    def steps

    Utils(steps) {
        this.steps = steps
    }

    def loadConfig(String resourcePath) {
        def content = steps.libraryResource(resourcePath)
        def props = new Properties()
        props.load(new StringReader(content))
        return props
    }
}
