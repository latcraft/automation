package lv.latcraft.event.tasks.router

import com.amazonaws.services.lambda.runtime.Context
import lv.latcraft.event.lambda.InternalContext
import lv.latcraft.event.tasks.BaseTask

class CraftBotCommands extends BaseTask {

  Map<String, String> doExecute(Map<String, String> input, Context context) {
    // TODO: process commands and call other lambdas
    // hello
    // help <command> <subcommand>
    // list venues
    // list suppression
    // status event
    // status eventbrite
    // status sendgrid
    // status twitter
    // status lanyrd
    // sync contacts
    // publish eventbrite
    // publish sendgrid
    // publish cards
    // publish twitter
    // send campaign
    // send reminder
    [:]
  }

  public static void main(String[] args) {
    new CraftBotCommands().execute([:], new InternalContext())
  }

}
