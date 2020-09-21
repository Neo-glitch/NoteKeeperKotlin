package com.neo.notekeeperkotlin

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat

/**
 * Helper class for showing and canceling reminder
 * notifications.
 *
 *
 * This class makes heavy use of the [NotificationCompat.Builder] helper
 * class to create notifications in a backward-compatible way.
 */
object ReminderNotification {
  /**
   * The unique identifier for this type of notification. used to get notification to cancel or update
   */
  private const val NOTIFICATION_TAG = "Reminder"

  const val REMINDER_CHANNEL = "reminders"

  const val KEY_TEXT_REPLY = "keyTextReply"

  /**
   * Shows the notification, or updates a previously shown notification of
   * this type, with the given parameters.
   *
   * @see .cancel
   */
  // overload used for messaging notif style
  fun notify(context: Context, note: NoteInfo, notePosition: Int) {

      // intent and pending for specialActivity
      val intent = NoteQuickViewActivity.getIntent(context, notePosition)
      intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

      val pendingIntentSpecial = PendingIntent.getActivity(context,
          0, intent,
          PendingIntent.FLAG_UPDATE_CURRENT)

      // var to hold bigTextStyle info
      val bigTextStyle = NotificationCompat.BigTextStyle()
          .bigText("just a random text to illustrate big Text style, and shows when the notification is expanded")
          .setBigContentTitle("Big content title")
          .setSummaryText("SummaryText")

      // var to hold BigPictureStyle
      val bigPictureStyle = NotificationCompat.BigPictureStyle()
          .bigPicture(BitmapFactory.decodeResource(context.resources, R.drawable.example_picture))
          // collapses imagePreview when notif is expanded
          .bigLargeIcon(null)

      // var for inboxStyle
      val inboxStyle = NotificationCompat.InboxStyle()
          .addLine("Message 1")
          .addLine("Message 2")
          .addLine("Message 3")
          .addLine("Message 4")
          .addLine("Message 5")

      // msg objects to go to the messagingStyle obj
      val message1 = NotificationCompat.MessagingStyle.Message(
          note.comments[0].comment,
          note.comments[0].timestamp,
          note.comments[0].name
      )
      val message2 = NotificationCompat.MessagingStyle.Message(
          note.comments[1].comment,
          note.comments[1].timestamp,
          note.comments[1].name
      )
      val message3 = NotificationCompat.MessagingStyle.Message(
          note.comments[2].comment,
          note.comments[2].timestamp,
          note.comments[2].name
      )

      // var for messagingStyle, "neo" is name that appears next to our msg when we reply to them
      val messagingStyle = NotificationCompat.MessagingStyle("Neo")
          .setConversationTitle(note.title)
          .addMessage(message3)
          .addMessage(message2)
          .addMessage(message1)

      // remote Input, key passed is used by reply intent to grap contents of this input
      val remoteInput = RemoteInput.Builder(KEY_TEXT_REPLY)
          .setLabel("Add Note")
          .build()

      // reply intent and pending intent that sends a broadCast
      val replyIntent = Intent(context, NotificationBroadcastReceiver::class.java)
      replyIntent.putExtra(NOTE_POSITION, notePosition)

      val replyPendingIntent = PendingIntent.getBroadcast(
          context, 100,
          replyIntent, PendingIntent.FLAG_UPDATE_CURRENT
      )

      // reply action
      val replyAction = NotificationCompat.Action
          .Builder(
              R.drawable.ic_action_stat_reply, "Add Note", replyPendingIntent
          )
          .addRemoteInput(remoteInput)
          .build()

      val pendingIntent = TaskStackBuilder.create(context)
          // intent to go to and also adds the intent parent and the parents parents... for best backStack exp
          .addNextIntentWithParentStack(intent)
          .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

      val shareIntent = PendingIntent.getActivity(context,
          0,
          // allows user to choose app for performing given action always
          Intent.createChooser(Intent(Intent.ACTION_SEND)
              .setType("text/plain")
              .putExtra(Intent.EXTRA_TEXT,
                  note.text.toString()
              ),
              "Share Note Reminder"),
          PendingIntent.FLAG_UPDATE_CURRENT)



      val builder = NotificationCompat.Builder(context, REMINDER_CHANNEL)

          // Set appropriate defaults for the notification light, sound,
          // and vibration.
          .setDefaults(Notification.DEFAULT_ALL)

          // Set required fields, including the small icon, the
          // notification title, and text
          .setSmallIcon(R.drawable.ic_stat_reminder)
          .setContentTitle("comments about: " + note.title)
          .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.example_picture))

          // All fields below this line are optional.

          // Use a default priority (recognized on devices running Android
          // 4.1 or later)
          .setPriority(NotificationCompat.PRIORITY_DEFAULT)

          // Set ticker text (preview) information for this notification. (used by accessibility readers)
          .setTicker("comments about: " + note.title)

          // Set the pending intent to be initiated when the user touches
          // the notification.
          .setContentIntent(pendingIntentSpecial)

              // sets color of replyText field
          .setColor(ContextCompat.getColor(context, R.color.darkOrange))
          .setColorized(true)

          .setOnlyAlertOnce(true)

          // Automatically dismiss the notification when it is touched.
          .setAutoCancel(true)
          .setStyle(messagingStyle)

          // Add a share action, but icon will not been shown on sdk higher than 7.0 but will show on wearables
          .addAction(R.drawable.ic_share_black_24dp,
              "Share", shareIntent)
              // adds action needed for quick reply using messagingStyle
          .addAction(replyAction)

      notify(context, builder.build())
  }

  // overload used for other notification styles
  fun notify(context: Context, titleText: String,
             noteText: String, notePosition: Int) {

      // intent and pending for specialActivity
      val intent = NoteQuickViewActivity.getIntent(context, notePosition)
      intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

      val pendingIntentSpecial = PendingIntent.getActivity(context,
      0, intent,
      PendingIntent.FLAG_UPDATE_CURRENT)

      // var to hold bigTextStyle info
      val bigTextStyle = NotificationCompat.BigTextStyle()
          .bigText("just a random text to illustrate big Text style, and shows when the notification is expanded")
          .setBigContentTitle("Big content title")
          .setSummaryText("SummaryText")

      // var to hold BigPictureStyle
      val bigPictureStyle = NotificationCompat.BigPictureStyle()
          .bigPicture(BitmapFactory.decodeResource(context.resources, R.drawable.example_picture))
          // collapses imagePreview when notif is expanded
          .bigLargeIcon(null)

      // var for inboxStyle
      val inboxStyle = NotificationCompat.InboxStyle()
          .addLine("Message 1")
          .addLine("Message 2")
          .addLine("Message 3")
          .addLine("Message 4")
          .addLine("Message 5")

      val pendingIntent = TaskStackBuilder.create(context)
          // intent to go to and also adds the intent parent and the parents parents... for best backStack exp
          .addNextIntentWithParentStack(intent)
          .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

    val shareIntent = PendingIntent.getActivity(context,
        0,
        // allows user to choose app for performing given action always
        Intent.createChooser(Intent(Intent.ACTION_SEND)
            .setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT,
                noteText
            ),
            "Share Note Reminder"),
        PendingIntent.FLAG_UPDATE_CURRENT)

    val builder = NotificationCompat.Builder(context, REMINDER_CHANNEL)

        // Set appropriate defaults for the notification light, sound,
        // and vibration.
        .setDefaults(Notification.DEFAULT_ALL)

        // Set required fields, including the small icon, the
        // notification title, and text.
        .setSmallIcon(R.drawable.ic_stat_reminder)
        .setContentTitle("5 new messages")
        .setContentText("Review your messgaes")
        .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.example_picture))

        // All fields below this line are optional.

        // Use a default priority (recognized on devices running Android
        // 4.1 or later)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Set ticker text (preview) information for this notification. (used by accessibility readers)
        .setTicker(titleText)

        // Set the pending intent to be initiated when the user touches
        // the notification.
        .setContentIntent(pendingIntentSpecial)

        // Automatically dismiss the notification when it is touched.
        .setAutoCancel(true)
        .setStyle(inboxStyle)

        // Add a share action, but icon will not been shown on sdk higher than 7.0 but will show on wearables
        .addAction(R.drawable.ic_share_black_24dp,
            "Share", shareIntent)

    notify(context, builder.build())
  }

  @TargetApi(Build.VERSION_CODES.ECLAIR)
  private fun notify(context: Context, notification: Notification) {
    val nm = context
        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
        // uses notificationManager to fireUp the notification
      nm.notify(NOTIFICATION_TAG, 0, notification)
    } else {
      nm.notify(NOTIFICATION_TAG.hashCode(), notification)
    }
  }

  /**
   * Cancels any notifications of this type previously shown using
   * [.notify].
   */
  @TargetApi(Build.VERSION_CODES.ECLAIR)
  fun cancel(context: Context) {
    val nm = context
        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
      nm.cancel(NOTIFICATION_TAG, 0)
    } else {
      nm.cancel(NOTIFICATION_TAG.hashCode())
    }
  }
}
