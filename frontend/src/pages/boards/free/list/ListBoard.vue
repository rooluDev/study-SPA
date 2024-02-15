<template>
  <h1>자유 게시판 - 목록</h1>
  <form @submit.prevent="getBoards(currentPageNum)">
    <input type="date" v-model="startDate" />
    <input type="date" v-model="endDate" />
    <select v-model="selectedCategoryId">
      <option value="-1">전체 카테고리</option>
      <option
        v-for="category in categoryList"
        :value="category.categoryId"
        :key="category.categoryId"
      >
        {{ category.categoryName }}
      </option>
    </select>
    <input type="text" v-model="searchText" required />
    <button type="submit">검색</button>
  </form>

  <div>
    <span>총 {{ boardCount }}건</span>
  </div>
  <table>
    <tr>
      <th>카테고리</th>
      <th>제목</th>
      <th>작성자</th>
      <th>조회수</th>
      <th>등록 일시</th>
      <th>수정 일시</th>
    </tr>
    <tr v-for="board in boardList" :key="board.boardId">
      <td>{{ board.categoryName }}</td>
      <td style="cursor: pointer" @click="goToView(board.boardId)">
        {{ board.title }}
      </td>
      <td>{{ board.userName }}</td>
      <td>{{ board.views }}</td>
      <td>{{ parseStringFormat(board.createdAt, 'YYYY-MM-DD HH:mm') }}</td>
      <td v-if="board.editedAt != null">
        {{ parseStringFormat(board.editedAt, 'YYYY-MM-DD HH:mm') }}
      </td>
      <td v-else>-</td>
    </tr>
  </table>
  <nav>
    <span v-for="page in numOfPages" :key="page" style="margin-right: 3px">
      <a @click="getBoards(page)">{{ page }}</a>
    </span>
  </nav>
  <button @click="goToWrite">등록</button>
</template>

<script>
import { ref, computed } from 'vue';
import axios from 'axios';
import { useRoute, useRouter } from 'vue-router';
import moment from 'moment';

export default {
  setup() {
    // router
    const router = useRouter();
    // 페이지에 사용될 변수들
    const boardList = ref([]);
    const categoryList = ref([]);
    const boardCount = ref(0);
    // 검색 조건
    const startDate = ref(useRoute().query.startDate);
    const endDate = ref(useRoute().query.endDate);
    const searchText = ref(useRoute().query.searchText);
    const selectedCategoryId = ref(useRoute().query.category);

    // 현제 페이지
    const currentPageNum = ref(useRoute().query.pageNum);
    // TODO : 페이지네이션 front or back
    // 페이지네이션
    const pageSize = 2;
    const numOfPages = computed(() => {
      return Math.ceil(boardCount.value / pageSize);
    });

    /**
     * 검색조건에 따른 게시물 리스트 GET요청
     * @param page
     * @returns {Promise<void>}
     */
    const getBoards = async (page = 1) => {
      // 현재 페이지 설정
      currentPageNum.value = page;
      // 처음 요청 시 데이터 설정
      if (
        startDate.value ||
        endDate.value ||
        searchText.value ||
        selectedCategoryId.value
      ) {
        startDate.value = '';
        endDate.value = '';
        searchText.value = '';
        selectedCategoryId.value = -1;
      }

      try {
        // board 요청
        const res = await axios.get(
          `/api/boards?startDate=${startDate.value}&endDate=${endDate.value}&categoryId=${selectedCategoryId.value}&searchText=${searchText.value}&pageNum=${currentPageNum.value}`,
        );
        // 데이터 설정
        boardList.value = res.data.boardList;
        startDate.value = res.data.searchCondition.startDate;
        endDate.value = res.data.searchCondition.endDate;
        selectedCategoryId.value = res.data.searchCondition.categoryId;
        searchText.value = res.data.searchCondition.searchText;
        boardCount.value = res.data.countRow;
      } catch (error) {
        // 에러
        alert('something is wrong');
        return;
      }
    };

    /**
     * 검색 조건에 쓰일 카테고리 리스트 GET요청
     * @returns {Promise<void>}
     */
    const getCategoryList = async () => {
      try {
        const res = await axios.get('/api/categories');
        categoryList.value = res.data;
      } catch (error) {
        alert('something is wrong');
        return;
      }
    };

    /**
     * 날짜 데이터 스트링 포맷으로 파싱
     * @param timestamp
     * @param format
     * @returns {string}
     */
    const parseStringFormat = (timestamp, format) => {
      return moment(timestamp).format(format);
    };

    /**
     * 게시판 - 보기 페이지로 이동
     * @param boardId
     */
    const goToView = (boardId) => {
      router.push({
        name: 'View',
        params: {
          id: boardId,
        },
        query: {
          startDate: parseStringFormat(startDate.value, 'YYYY-MM-DD'),
          endDate: parseStringFormat(endDate.value, 'YYYY-MM-DD'),
          category: selectedCategoryId.value,
          searchText: searchText.value,
          pageNum: currentPageNum.value,
        },
      });
    };

    /**
     * 게시판 - 등록 페이지 이동
     */
    const goToWrite = () => {
      router.push({
        name: 'Write',
        query: {
          startDate: parseStringFormat(startDate.value, 'YYYY-MM-DD'),
          endDate: parseStringFormat(endDate.value, 'YYYY-MM-DD'),
          category: selectedCategoryId.value,
          searchText: searchText.value,
          pageNum: currentPageNum.value,
        },
      });
    };

    getBoards(currentPageNum.value);
    getCategoryList();

    return {
      boardList,
      categoryList,
      boardCount,
      searchText,
      startDate,
      endDate,
      numOfPages,
      currentPageNum,
      getBoards,
      goToView,
      goToWrite,
      parseStringFormat,
      selectedCategoryId,
    };
  },
};
</script>

<style></style>
