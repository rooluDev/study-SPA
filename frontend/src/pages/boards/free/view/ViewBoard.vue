<template>
  <h1>게시판 - 보기</h1>
  <div v-if="board != null && category != null">
    <span>글쓴이</span>
    <p>{{ board.userName }}</p>
    <span>등록일시</span>
    <p>{{ parseFormat(board.createdAt, 'YYYY-MM-DD HH:mm') }}</p>
    <span>수정일시</span>
    <p v-if="board.editedAt != null">
      {{ parseFormat(board.editedAt, 'YYYY-MM-DD HH:mm') }}
    </p>
    <p v-else>-</p>
    <span>카테고리</span>
    <p>{{ category.categoryName }}</p>
    <span>제목</span>
    <p>{{ board.title }}</p>
    <span>조회수</span>
    <p>{{ board.views }}</p>
    <span>내용</span>
    <p>{{ board.content }}</p>
    <p>첨부파일</p>
    <p
      v-if="fileList != null"
      v-for="file in fileList"
      :key="file.fileId"
      @click="downloadFile(file.fileId)"
    >
      {{ file.originalName }}
    </p>
    <span>댓글</span>
    <div
      v-if="commentList != null"
      v-for="comment in commentList"
      :key="comment.commentId"
    >
      <p>{{ parseFormat(comment.createdAt, 'YYYY-MM-DD HH:mm') }}</p>
      <p>{{ comment.comment }}</p>
    </div>
    <form @submit.prevent="registerComment()">
      <input type="text" v-model="comment" />
      <button type="submit">등록</button>
    </form>
  </div>
  <button @click="goToList">목록</button>
  <button @click="goToModify">수정</button>
  <button @click="deleteButton">삭제</button>

  <div v-show="showDeleteButton">
    <label>비밀번호</label>
    <input type="password" v-model="password" />
    <button @click="deleteBoard">입력</button>
    <button @click="deleteButton">취소</button>
  </div>
</template>
<script>
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import { ref } from 'vue';
import moment from 'moment/moment';

export default {
  setup() {
    const route = useRoute();
    const router = useRouter();
    const boardId = route.params.id;
    // 검색 조건 쿼리 데이터
    const startDate = route.query.startDate;
    const endDate = route.query.endDate;
    const categoryId = route.query.category;
    const searchText = route.query.searchText;
    const pageNum = route.query.pageNum;
    // 페이지 사용될 변수들
    const board = ref(null);
    const category = ref(null);
    const commentList = ref([]);
    const fileList = ref([]);
    const comment = ref('');

    const password = ref('');
    const showDeleteButton = ref(false);

    /**
     * 게시물 GET요청
     * @returns {Promise<void>}
     */
    const getBoard = async () => {
      try {
        const res = await axios.get(`/api/board/${boardId}`);
        board.value = res.data;
      } catch (error) {
        alert('something is wrong');
        return;
      }
      // 카테고리 가져오기
      await getCategory(board.value.categoryId);
    };

    /**
     * categoryId에 맞는 카테고리 GET 요청
     * @param categoryId
     * @returns {Promise<void>}
     */
    const getCategory = async (categoryId) => {
      try {
        const res = await axios.get(`/api/category/${categoryId}`);
        category.value = res.data;
      } catch (error) {
        alert('something is wrong');
        return;
      }
    };

    /**
     * 파일 리스트 GET 요청
     * @returns {Promise<void>}
     */
    const getFileList = async () => {
      try {
        const res = await axios.get(`/api/files/${boardId}`);
        fileList.value = res.data;
      } catch (error) {
        alert('something is wrong');
        return;
      }
    };

    /**
     * 댓글들 GET 요청
     * @returns {Promise<void>}
     */
    const getCommentList = async () => {
      try {
        const res = await axios.get(`/api/comment/${boardId}`);
        commentList.value = res.data;
      } catch (error) {
        alert('something is wrong');
        return;
      }
    };

    /**
     * 댓글 POST 요청
     * @returns {Promise<void>}
     */
    const registerComment = async () => {
      try {
        await axios.post(`/api/comment`, {
          boardId: boardId,
          comment: comment.value,
        });
        // 댓글 등록 후 댓글 리스트 가져오기
        await getCommentList();
        comment.value = '';
      } catch (error) {
        alert('something is wrong');
        return;
      }
    };

    /**
     * 비밀번호 확인 POST 요청
     * @returns {Promise<boolean>}
     */
    const passwordConfirm = async () => {
      try {
        await axios.post(`/api/board/${boardId}/check-password`, {
          password: password.value,
        });
        return true;
      } catch (error) {
        alert(error.response.data);
        password.value = '';
        return false;
      }
    };

    /**
     * 게시물 DELETE 요청
     * @returns {Promise<void>}
     */
    const deleteBoard = async () => {
      const res = await passwordConfirm();
      if (res) {
        try {
          await axios.delete(`/api/board/${boardId}`);
          // 삭제 후 목록 페이지 이동
          goToList();
        } catch (error) {
          alert('something is wrong');
        }
      }
    };

    /**
     * 파일 다운로드 GET 요청
     * @param fileId
     * @returns {Promise<void>}
     */
    const downloadFile = async (fileId) => {
      try {
        const res = await axios.get(`/api/file/${fileId}/download`, {
          responseType: 'blob',
        });
        let fileName = '';
        for (let i = 0; i < fileList.value.length; i++) {
          if (fileList.value[i].fileId == fileId) {
            fileName = fileList.value[i].originalName;
          }
        }
        // download object 설정
        const url = window.URL.createObjectURL(new Blob([res.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', `${fileName}`);
        document.body.appendChild(link);

        link.click();
        link.remove();
      } catch (error) {
        alert('something is wrong');
      }
    };

    /**
     * 조회수 증가 PATCH
     * @returns {Promise<void>}
     */
    const increaseView = async () => {
      try {
        await axios.patch(`/api/board/${boardId}/increase-views`);
      } catch (error) {
        alert('something is wrong');
      }
    };

    /**
     * string format으로 파싱
     * @param timestamp
     * @param format
     * @returns {string}
     */
    const parseFormat = (timestamp, format) => {
      return moment(timestamp).format(format);
    };

    /**
     * 비밀번호 확인 창
     */
    const deleteButton = () => {
      showDeleteButton.value = !showDeleteButton.value;
    };

    const goToList = () => {
      router.push({
        name: 'List',
        query: {
          startDate: startDate,
          endDate: endDate,
          category: categoryId,
          searchText: searchText,
          pageNum: pageNum,
        },
      });
    };

    const goToModify = () => {
      router.push({
        name: 'Modify',
        query: {
          boardId: boardId,
          startDate: startDate,
          endDate: endDate,
          category: categoryId,
          searchText: searchText,
          pageNum: pageNum,
        },
      });
    };

    getBoard();
    getFileList();
    getCommentList();
    increaseView();

    return {
      board,
      category,
      commentList,
      fileList,
      comment,
      password,
      showDeleteButton,
      goToList,
      parseFormat,
      registerComment,
      deleteBoard,
      goToModify,
      deleteButton,
      downloadFile,
    };
  },
};
</script>
<style></style>
