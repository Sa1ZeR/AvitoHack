<template>
    <form @submit.prevent>
        <h4>Создание поста</h4>
        <input 
            v-bind:value="title"
            @input="inputTitle" 
            class="input" 
            type="text" 
            placeholder="Описание"
        >
        <input 
            v-bind:value="body"
            @input="inputBody" 
            class="input" 
            type="text" 
            placeholder="Название"
        >
        <button 
            class="btn" 
        >
            Создать
        </button>
    </form>
</template>

<script>
export default {
  name: "post-form"
}
</script>

<style>
form {
    display: flex;
    flex-direction: column;
}
.input {
    width: 100%;
    border: 1px solid teal;
    padding: 10px 15px;
    margin-top: 15px;
}
.btn {
    margin-top: 15px;
    align-self: flex-end;
    padding: 10px 15px;
    color: teal;
    border: 1px solid teal;
}
</style>