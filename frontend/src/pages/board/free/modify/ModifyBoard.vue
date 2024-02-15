<template>
  <h1>게시판 - 수정</h1>
  <div v-if="board != null && category != null">
    <span>카테고리</span>
    <p>{{ category.categoryName }}</p>
    <span>등록일시</span>
    <p>{{ parseFormat(board.createdAt, 'YYYY-MM-DD HH:mm') }}</p>
    <span>수정 일시</span>
    <p v-if="board.editedAt != null">
      {{ parseFormat(board.editedAt, 'YYYY-MM-DD HH:mm') }}
    </p>
    <p v-else>-</p>
    <span>조회수</span>
    <p>{{ board.views }}</p>
    <form @submit.prevent="updateBoard">
      <span>작성자</span>
      <input type="text" v-model="board.userName" />
      <br />
      <span>비밀번호</span>
      <input type="password" v-model="password" />
      <br />
      <span>제목</span>
      <input type="text" v-model="board.title" />
      <br />
      <span>내용</span>
      <input type="text" v-model="board.content" />
      <br />
      <span>파일 첨부</span>
      <div v-for="(file, index) in fileList">
        <span>{{ file.originalName }}</span>
        <button @click.stop="selectedFile(file.fileId, index)">X</button>
      </div>
      <br />
      <button type="submit">저장</button>
    </form>
    <button @click="goToView">취소</button>
  </div>
</template>
<script>
import { useRouter, useRoute } from 'vue-router';
import axios from 'axios';
import { ref } from 'vue';
import moment from 'moment/moment';

export default {
  setup() {
    const router = useRouter();
    const route = useRoute();
    // 검색조건 저장
    const startDate = route.query.startDate;
    const endDate = route.query.endDate;
    const categoryId = route.query.category;
    const searchText = route.query.searchText;
    const pageNum = route.query.pageNum;
    const boardId = route.query.boardId;
    // 페이지에 쓰는 데이터 설정
    const board = ref(null);
    const category = ref(null);
    const fileList = ref([]);
    // input data
    const password = ref('');
    const deletedFilesList = ref([]);

    /**
     * 게시물 데이터 GET
     * @returns {Promise<void>}
     */
    const getBoard = async () => {
      try {
        const res = await axios.get(`/api/board/${boardId}`);
        // 데이터 설정
        board.value = res.data;
        board.value.userName = res.data.userName;
        board.value.title = res.data.title;
        board.value.content = res.data.content;
      } catch (error) {
        alert('something is wrong');
      }
      await getCategory(board.value.categoryId);
    };

    /**
     * 카테고리 아이디로 카테고리 GET
     * @param categoryId
     * @returns {Promise<void>}
     */
    const getCategory = async (categoryId) => {
      try {
        const res = await axios.get(`/api/category/${categoryId}`);
        category.value = res.data;
      } catch (error) {
        alert('something is wrong');
      }
    };

    /**
     * 파일 리스트 GET
     * @returns {Promise<void>}
     */
    const getFileList = async () => {
      try {
        const res = await axios.get(`/api/files/${boardId}`);
        fileList.value = res.data;
      } catch (error) {
        alert('something is wrong');
      }
    };

    /**
     * 비밀번호 확인 POST
     * @returns {Promise<boolean>}
     */
    const checkPassword = async () => {
      try {
        await axios.post(`/api/board/${boardId}/check-password`, {
          password: password.value,
        });
        return true;
      } catch (error) {
        alert('password incorrect');
        password.value = '';
        return false;
      }
    };

    /**
     * 게시물 수정 PUT
     * @returns {Promise<void>}
     */
    const updateBoard = async () => {
      const res = await checkPassword();
      try {
        if (res) {
          await axios.put(`/api/board/${boardId}`, {
            userName: board.value.userName,
            title: board.value.title,
            content: board.value.content,
          });
          // 삭제 할 파일리스트 반복문 돌면서 삭제
          for (let i = 0; i < deletedFilesList.value.length; i++) {
            await deleteFile(deletedFilesList.value[i]);
          }
          // 게시판 - 보기 페이지 이동
          goToView();
        }
      } catch (error) {
        alert('something is wrong');
      }
    };

    /**
     * 파일 삭제 DELETE
     * @param fileId
     * @returns {Promise<void>}
     */
    const deleteFile = async (fileId) => {
      await axios.delete(`/api/file/${fileId}`);
    };

    /**
     * 파일 X 버튼 누르면 fileId 저장, 파일 리스트에서 삭제
     * @param fileId
     * @param index
     */
    const selectedFile = (fileId, index) => {
      deletedFilesList.value.push(fileId);
      fileList.value.splice(index, 1);
    };

    /**
     * String format으로 파싱
     * @param timestamp
     * @param format
     * @returns {string}
     */
    const parseFormat = (timestamp, format) => {
      return moment(timestamp).format(format);
    };

    getBoard();
    getFileList();

    const goToView = () => {
      router.push({
        name: 'View',
        params: {
          id: boardId,
        },
        query: {
          startDate: startDate,
          endDate: endDate,
          category: categoryId,
          searchText: searchText,
          pageNum: pageNum,
        },
      });
    };
    return {
      board,
      category,
      fileList,
      password,
      goToView,
      parseFormat,
      updateBoard,
      selectedFile,
    };
  },
};
</script>
<style></style>
