<template>
  <h1>게시판 - 등록</h1>
  <form @submit.prevent="uploadBoard">
    <label>카테고리</label>
    <select v-model="selectedCategoryId">
      <option value="-1">카테고리 선택</option>
      <option
        v-for="category in categoryList"
        :value="category.categoryId"
        :key="category.categoryId"
      >
        {{ category.categoryName }}
      </option>
    </select>
    <br />
    <label>작성자</label>
    <input
      type="text"
      v-model="userName"
      minlength="3"
      maxlength="4"
      required
    />
    <br />
    <label>비밀번호</label>
    <input
      type="password"
      v-model="password"
      minlength="4"
      maxlength="15"
      required
    />
    <input
      type="password"
      v-model="passwordCheck"
      minlength="4"
      maxlength="15"
      required
    />
    <br />
    <label>제목</label>
    <input type="text" v-model="title" minlength="4" maxlength="99" required />
    <br />
    <label>내용</label>
    <input
      type="text"
      v-model="content"
      minlength="4"
      maxlength="1999"
      required
    />
    <br />
    <input type="file" @change="fileInput" />
    <input type="file" @change="fileInput" />
    <input type="file" @change="fileInput" />
    <br />
    <button type="submit">저장</button>
    <button @click="goToList">취소</button>
  </form>
</template>
<script>
import { ref } from 'vue';
import axios from 'axios';
import { useRoute, useRouter } from 'vue-router';
export default {
  setup() {
    // param 설정
    const router = useRouter();
    const route = useRoute();
    const startDate = route.query.startDate;
    const endDate = route.query.endDate;
    const category = route.query.category;
    const searchText = route.query.searchText;
    const pageNum = route.query.pageNum;
    // input data
    const categoryList = ref([]);
    const selectedCategoryId = ref(0);
    const userName = ref('');
    const password = ref('');
    const passwordCheck = ref('');
    const title = ref('');
    const content = ref('');
    const files = ref([]);

    /**
     * input 카테고리 리스트
     * @returns {Promise<void>}
     */
    const getCategoryList = async () => {
      const res = await axios.get('/api/categories');
      categoryList.value = res.data;
    };

    /**
     * 파일 등록시 파일 push
     * @param event
     */
    const fileInput = (event) => {
      files.value = files.value.concat(Array.from(event.target.files));
    };

    /**
     * 파일 클릭시 파일 다운로드
     * @returns {Promise<void>}
     */
    const uploadBoard = async () => {
      // 비밀번호 확인
      if (!checkPasswordRegex() || !checkPasswordEqual()) {
        alert('Something is Wrong');
        return;
      }
      // formdata 설정
      const formData = new FormData();
      // 파일 부분 설정
      files.value.forEach((file) => {
        formData.append('files', file);
      });
      // text 부분 설정
      formData.append('categoryId', selectedCategoryId.value);
      formData.append('userName', userName.value);
      formData.append('password', password.value);
      formData.append('passwordCheck', passwordCheck.value);
      formData.append('title', title.value);
      formData.append('content', content.value);
      // 파일 저장 POST
      try {
        await axios.post('/api/board', formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        });
        goToList();
      } catch (error) {
        alert(error.response.data);
      }
    };

    /**
     * 비밀번호, 비밀번호 확인 일치 여부
     * @returns {boolean}
     */
    const checkPasswordEqual = () => {
      if (password.value === passwordCheck.value) {
        return true;
      } else {
        return false;
      }
    };
    /**
     * 비밀번호 regex 확인
     * @returns {boolean}
     */
    const checkPasswordRegex = () => {
      const regex = /^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*?_]).{4,15}$/;
      if (regex.test(password.value)) {
        return true;
      } else {
        return false;
      }
    };

    const goToList = () => {
      router.push({
        name: 'List',
        query: {
          startDate: startDate,
          endDate: endDate,
          category: category,
          searchText: searchText,
          pageNum: pageNum,
        },
      });
    };

    getCategoryList();

    return {
      categoryList,
      selectedCategoryId,
      userName,
      password,
      passwordCheck,
      title,
      content,
      fileInput,
      uploadBoard,
      goToList,
    };
  },
};
</script>
<style></style>
