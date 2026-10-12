<template>
  <div
    v-show="isAuthorized"
    v-if="isAuthorized"
    class="hexagon-wrapper"
    :aria-label="`Navigate to ${titleAr}`"
    @click="navigateTo"
  >
    <div class="hexagon">
      <div class="hexagon-content">
        <div v-if="icon" class="hexagon-icon">
          <v-icon :color="iconColor" alt="icon" :size="iconSize" class="icon-image">
            {{ icon }}
          </v-icon>
        </div>
        <div v-else-if="activityIndicator" class="hexagon-icon custom-hexagon-spinners-icon-style">
          <AppActivityIndicator :color="activityIndicatorColor" :size="activityIndicatorSize" aria-label="Active" />
        </div>
        <h3 class="hexagon-title font-bukra-bold">{{ titleAr }}</h3>
        <p class="hexagon-description font-dubai-regular">{{ titleEn }}</p>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import { useRouter } from 'vue-router'
import AppActivityIndicator from '@/components/common/AppActivityIndicator.vue'

export default defineComponent({
  name: 'HexagonComponent',
  components: {
    AppActivityIndicator
  },
  props: {
    titleAr: {
      type: String,
      required: true
    },
    titleEn: {
      type: String,
      required: true
    },
    route: {
      type: String,
      required: true
    },
    isAuthorized: {
      type: Boolean,
      default: false
    },
    icon: {
      type: String,
      default: ''
    },
    iconColor: {
      type: String,
      default: 'green'
    },
    iconSize: {
      type: String,
      default: 'large'
    },
    activityIndicatorColor: {
      type: String,
      default: 'green'
    },
    activityIndicatorSize: {
      type: [String, Number],
      default: '30'
    },
    activityIndicator: {
      type: Boolean,
      required: false,
      default: false
    }
  },

  emits: ['navigate'],
  setup(props, { emit }) {
    const router = useRouter()

    const navigateTo = () => {
      router.push({ path: props.route })
      emit('navigate', props.route)
    }

    return {
      navigateTo
    }
  }
})
</script>

<style scoped lang="scss">
@use '@/assets/css/variables' as *;

$primary-color: $color-gray-lighter;
$border-color: $sdss-primary-color;
$border-width: 0.5%;

@mixin hexagon-shape($color) {
  width: 280px;
  height: 240px;
  background-color: $color;
  clip-path: polygon(20% 0%, 80% 0%, 100% 50%, 80% 100%, 20% 100%, 0% 50%);
  cursor: pointer;
  transition: transform 0.3s,
  box-shadow 0.3s;
  box-sizing: border-box;
}

.hexagon-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
  width: fit-content;
}

.hexagon-wrapper {
  @include hexagon-shape($border-color);
  padding: $border-width;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

.hexagon {
  @include hexagon-shape($primary-color);
  width: calc(240px - 2 * $border-width);
  height: calc(200px - 2 * $border-width);
  position: relative;
}

.hexagon-content {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  pointer-events: none;
  font-family: $font-family;
}

.hexagon-title {
  font-size: 1rem;
  color: $primary-black-text-color;
  font-weight: 700;
  letter-spacing: 1px;
  text-transform: uppercase;
  text-align: center;
  margin-bottom: 20%;
}

.hexagon-wrapper:hover {
  transform: scale(1.35);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}

.hexagon-description {
  font-size: 1rem;
  color: $primary-black-text-color;
  font-weight: 400;
  line-height: 1.5;
  text-align: center;
}

.hexagon-icon {
  margin-bottom: 10px;
  text-align: center;

  .icon-image {
    width: 40px;
    height: 40px;
    display: inline-block;
    pointer-events: none;
  }
}

.custom-hexagon-spinners-icon-style {
  display: flex;
  justify-content: center;
  justify-items: center;
}
</style>
