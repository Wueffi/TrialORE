package org.openredstone.trialore

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import org.bukkit.entity.Player

@CommandAlias("test")
@CommandPermission("trialore.test")
class TestCommand(
    private val trialORE: TrialOre,
) : BaseCommand() {

    @Default()
    @CommandAlias("starttest")
    @Subcommand("start")
    @Conditions("notTesting")
    @Description("Take a test")
    fun onStart(player: Player) {
        val testificate = player
        if (trialORE.testMapping.filter { (uuid) ->
                uuid == testificate.uniqueId
            }.isNotEmpty()) {
            throw TrialOreException("You are already testing. This is 99.9% a Bug. Contact Nick :D")
        }
        testificate.renderMessage("Starting your test!")
        trialORE.startTest(testificate.uniqueId)
    }

    @Subcommand("list")
    @CommandPermission("trialore.list")
    @Description("Get the info of an individual from past tests")
    @CommandCompletion("@usernameCache")
    fun onList(player: Player, @Single target: String) {
        var testificate = trialORE.server.onlinePlayers.firstOrNull { it.name == target }?.uniqueId
        if (testificate == null) {
            testificate = trialORE.database.usernameToUuidCache[target]
                ?: throw TrialOreException("Invalid target $target. Please provide an online player or UUID")
        }
        val tests = trialORE.database.getTests(testificate)
        player.renderMiniMessage("<gray>$target has taken ${tests.size} tests")
        tests.forEachIndexed { index, test ->
            val testInfo = trialORE.database.getTestInfo(test)
            val state = if (testInfo.passed) {
                "<green>Passed</green>"
            } else {
                "<red>Failed</red>"
            }
            val startTime = testInfo.start.toLong()
            val timestamp = getRelativeTimestamp(startTime)
            val wrong = testInfo.wrong
            val correct = 25 - wrong
            val percentage = (correct.toDouble() / 25.toDouble()) * 100
            player.renderMiniMessage("<hover:show_text:'At <gray>${getDate(startTime)}<white>" +
                    " (State: ${state}), with ${wrong} wrong Answers (${percentage}) '><gray>Test ${index+1}, $timestamp</hover>:")
        }
    }

    @Subcommand("history")
    @CommandPermission("trialore.test")
    @Description("Get your test history")
    fun onHistory(player: Player) {
        val testificate = player.uniqueId
        val tests = trialORE.database.getTests(testificate)
        player.renderMiniMessage("<gray>You have taken ${tests.size} tests")
        tests.forEachIndexed { index, test ->
            val testInfo = trialORE.database.getTestInfo(test)
            val state = if (testInfo.passed) {
                "<green>Passed</green>"
            } else {
                "<red>Failed</red>"
            }
            val startTime = testInfo.start.toLong()
            val timestamp = getRelativeTimestamp(startTime)
            val wrong = testInfo.wrong
            val correct = 25 - wrong
            val percentage = (correct.toDouble() / 25.toDouble()) * 100
            player.renderMiniMessage("<hover:show_text:'At <gray>${getDate(startTime)}<white>" +
                    " (State: ${state}), with ${wrong} wrong Answers'><gray>Test ${index+1}, $timestamp</hover>: $percentage%")
        }
    }

    @CommandAlias("stoptest")
    @Subcommand("stop")
    @Conditions("testing")
    @Description("Stop a test")
    fun onStop(player: Player, testMeta: TestMeta) {
        player.renderMessage("You have exited your test")
        trialORE.endTest(player.uniqueId, testMeta.testId, false, wrong = 25)
    }

    @CommandAlias("testanswer")
    @Subcommand("answer")
    @Conditions("testing")
    @Description("Answer a test question")
    fun onAnswer(player: Player, testMeta: TestMeta, answer: String) {
        player.renderMessage("Recieved Test-Answer: $answer")
    }
}
