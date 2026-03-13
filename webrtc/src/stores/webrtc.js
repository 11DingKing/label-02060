import { defineStore } from "pinia";
import { ref, computed, shallowRef } from "vue";

export const useWebRTCStore = defineStore("webrtc", () => {
  const localStream = ref(null);
  const remoteStreams = shallowRef(new Map());
  const users = ref([]);
  const videoEnabled = ref(true);
  const audioEnabled = ref(true);

  const remoteStreamList = computed(() =>
    Array.from(remoteStreams.value.entries()),
  );

  function setLocalStream(stream) {
    localStream.value = stream;
  }

  function addRemoteStream(userId, stream) {
    const newMap = new Map(remoteStreams.value);
    newMap.set(userId, stream);
    remoteStreams.value = newMap;
  }

  function removeRemoteStream(userId) {
    const newMap = new Map(remoteStreams.value);
    newMap.delete(userId);
    remoteStreams.value = newMap;
  }

  function setUsers(userList) {
    users.value = [...userList];
  }

  function toggleVideo() {
    if (localStream.value) {
      const track = localStream.value.getVideoTracks()[0];
      if (track) {
        track.enabled = !track.enabled;
        videoEnabled.value = track.enabled;
      }
    }
  }

  function toggleAudio() {
    if (localStream.value) {
      const track = localStream.value.getAudioTracks()[0];
      if (track) {
        track.enabled = !track.enabled;
        audioEnabled.value = track.enabled;
      }
    }
  }

  function cleanup() {
    if (localStream.value) {
      localStream.value.getTracks().forEach((track) => track.stop());
      localStream.value = null;
    }
    remoteStreams.value = new Map();
    users.value = [];
    videoEnabled.value = true;
    audioEnabled.value = true;
  }

  return {
    localStream,
    remoteStreams,
    remoteStreamList,
    users,
    videoEnabled,
    audioEnabled,
    setLocalStream,
    addRemoteStream,
    removeRemoteStream,
    setUsers,
    toggleVideo,
    toggleAudio,
    cleanup,
  };
});
