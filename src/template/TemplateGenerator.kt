package template

import io.Directories
import io.FileWriter

object TemplateGenerator {

    fun makeSettingsFile(projectDirectory: String) {
        FileWriter.writeFile(Directories.getDirectory(projectDirectory, "settings.vmod"),
                "id:       my_mod",
                "name:     My Great Mod",
                "version:  0.0.1",
                "output:   "
        )
    }

    fun makeExampleProject(projectDirectory: String) {
        makeSettingsFile(projectDirectory)

        FileWriter.writeFile(Directories.getDirectory(projectDirectory, "example_item.vlcn"),
                "type: item",
                "",
                "attributes:",
                "    set name to \"Example\"",
                "    set description to \"This is an example item. Replace whatever you want!\"",
                "    set shiny to true",
                "",
                "right_click:",
                "    tell player to burn for 5 seconds",
                "",
                "hit_entity:",
                "    tell attacker to ride target",
                "    tell target to heal 1 health"
        )
    }
}