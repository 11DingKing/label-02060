import axios from "axios";

const api = axios.create({
  baseURL: "/api",
  timeout: 10000,
});

api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    console.error("API Error:", error);
    return Promise.reject(error);
  },
);

export const createRoom = (data) => api.post("/room/create", data);
export const getRoomList = () => api.get("/room/list");
export const getRoomInfo = (roomId) => api.get(`/room/${roomId}`);
export const deleteRoom = (roomId) => api.delete(`/room/${roomId}`);
