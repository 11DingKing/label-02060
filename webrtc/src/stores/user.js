import { defineStore } from "pinia";
import { ref } from "vue";

export const useUserStore = defineStore("user", () => {
  const userId = ref(localStorage.getItem("userId") || generateId());
  const userName = ref(localStorage.getItem("userName") || "");

  function generateId() {
    const id = "user_" + Math.random().toString(36).substring(2, 10);
    localStorage.setItem("userId", id);
    return id;
  }

  function setUserName(name) {
    userName.value = name;
    localStorage.setItem("userName", name);
  }

  return { userId, userName, setUserName };
});
