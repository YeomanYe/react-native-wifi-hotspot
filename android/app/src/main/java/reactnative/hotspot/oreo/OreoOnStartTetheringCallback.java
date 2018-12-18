package reactnative.hotspot.oreo;

public abstract class OreoOnStartTetheringCallback {
        /**
         * Called when tethering has been successfully started.
         */
        public abstract void onTetheringStarted();

        /**
         * Called when starting tethering failed.
         */
        public abstract void onTetheringFailed();

}