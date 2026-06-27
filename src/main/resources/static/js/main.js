/**
 * GameStore Platform — JavaScript Vanilla
 */
(function () {
  "use strict";

  document.addEventListener("DOMContentLoaded", function () {
    initMobileMenu();
    initSearch();
    initUserDropdown();
    initToasts();
    initHeroCarousel();
    initFlashCountdown();
    initCatalogView();
    initFilterSidebar();
    initAddToCartButtons();
    initImageZoom();
    initGallery();
    initTabs();
    initReviewStars();
    initAuthForms();
    initCart();
    initPromoCode();
    initCheckout();
    initAccountNav();
    initKeyReveal();
    initOrderModal();
    initAdminModals();
    initMaintenanceCountdown();
    initEmailVerification();
  });

  function initMobileMenu() {
    var toggle = document.querySelector("[data-menu-toggle]");
    var menu = document.querySelector("[data-mobile-menu]");
    if (!toggle || !menu) return;
    toggle.addEventListener("click", function () {
      var open = toggle.getAttribute("aria-expanded") === "true";
      toggle.setAttribute("aria-expanded", String(!open));
      menu.setAttribute("aria-hidden", String(open));
      document.body.classList.toggle("overflow-hidden", !open);
    });
    menu.querySelectorAll("a").forEach(function (a) {
      a.addEventListener("click", function () {
        toggle.setAttribute("aria-expanded", "false");
        menu.setAttribute("aria-hidden", "true");
        document.body.classList.remove("overflow-hidden");
      });
    });
    var searchToggle = document.querySelector("[data-search-toggle]");
    var searchPanel = document.querySelector("[data-search-panel-mobile]");
    if (searchToggle && searchPanel) {
      searchToggle.addEventListener("click", function () { searchPanel.classList.toggle("hidden"); });
    }
  }

  function initSearch() {
    document.querySelectorAll("[data-search-input]").forEach(function (input) {
      var wrapper = input.closest("[data-search-wrapper]");
      var autocomplete = wrapper ? wrapper.querySelector("[data-search-autocomplete]") : null;
      input.addEventListener("focus", function () { if (autocomplete) autocomplete.classList.remove("hidden"); });
      input.addEventListener("blur", function () {
        setTimeout(function () { if (autocomplete) autocomplete.classList.add("hidden"); }, 200);
      });
      if (autocomplete) {
        autocomplete.querySelectorAll("button").forEach(function (btn) {
          btn.addEventListener("mousedown", function (e) {
            e.preventDefault();
            input.value = btn.textContent.replace(" →", "").trim();
            autocomplete.classList.add("hidden");
          });
        });
      }
    });
  }

  function initUserDropdown() {
    var toggle = document.querySelector("[data-user-dropdown-toggle]");
    var menu = document.querySelector("[data-user-dropdown]");
    if (!toggle || !menu) return;
    toggle.addEventListener("click", function (e) {
      e.stopPropagation();
      var open = !menu.classList.contains("hidden");
      menu.classList.toggle("hidden", open);
      toggle.setAttribute("aria-expanded", String(!open));
    });
    document.addEventListener("click", function () {
      menu.classList.add("hidden");
      toggle.setAttribute("aria-expanded", "false");
    });
  }

  window.showToast = function (type, message) {
    var container = document.querySelector("[data-toast-container]");
    var template = document.getElementById("toast-template");
    if (!container || !template) return;
    var node = template.content.cloneNode(true);
    var toast = node.querySelector("[role='alert']");
    var msg = node.querySelector("[data-toast-message]");
    var icon = node.querySelector("[data-toast-icon]");
    msg.textContent = message;
    var map = {
      success: { cls: "border-success/30 bg-success/10", icon: "✓", color: "text-success" },
      error: { cls: "border-error/30 bg-error/10", icon: "✕", color: "text-error" },
      info: { cls: "border-info/30 bg-info/10", icon: "ℹ", color: "text-info" }
    };
    var cfg = map[type] || map.info;
    toast.className += " " + cfg.cls;
    icon.textContent = cfg.icon;
    icon.className += " " + cfg.color + " font-bold";
    container.appendChild(node);
    var el = container.lastElementChild;
    el.querySelector("[data-toast-close]").addEventListener("click", function () { el.remove(); });
    setTimeout(function () { if (el.parentNode) el.remove(); }, 5000);
  };

  function initToasts() {
    document.querySelectorAll("[data-flash-type]").forEach(function (el) {
      showToast(el.getAttribute("data-flash-type"), el.getAttribute("data-flash-message"));
    });
  }

  function initHeroCarousel() {
    var carousel = document.querySelector("[data-hero-carousel]");
    if (!carousel) return;
    var slides = carousel.querySelectorAll("[data-hero-slide]");
    var dots = carousel.querySelectorAll("[data-hero-dot]");
    var prev = carousel.querySelector("[data-hero-prev]");
    var next = carousel.querySelector("[data-hero-next]");
    var current = 0, timer;
    function goTo(i) {
      current = (i + slides.length) % slides.length;
      slides.forEach(function (s, idx) { s.classList.toggle("active", idx === current); });
      dots.forEach(function (d, idx) {
        d.classList.toggle("bg-brand-cyan", idx === current);
        d.classList.toggle("bg-border", idx !== current);
      });
    }
    function auto() { timer = setInterval(function () { goTo(current + 1); }, 6000); }
    if (prev) prev.addEventListener("click", function () { clearInterval(timer); goTo(current - 1); auto(); });
    if (next) next.addEventListener("click", function () { clearInterval(timer); goTo(current + 1); auto(); });
    dots.forEach(function (d, i) { d.addEventListener("click", function () { clearInterval(timer); goTo(i); auto(); }); });
    goTo(0); auto();
  }

  function initFlashCountdown() {
    document.querySelectorAll("[data-flash-timer]").forEach(function (el) {
      var end = parseInt(el.getAttribute("data-end") || "0", 10) || Date.now() + 16335000;
      function tick() {
        var diff = Math.max(0, end - Date.now());
        var h = Math.floor(diff / 3600000), m = Math.floor((diff % 3600000) / 60000), s = Math.floor((diff % 60000) / 1000);
        el.textContent = String(h).padStart(2, "0") + "h " + String(m).padStart(2, "0") + "m " + String(s).padStart(2, "0") + "s";
        if (diff > 0) requestAnimationFrame(tick);
      }
      tick();
    });
  }

  function initCatalogView() {
    var wrapper = document.querySelector("[data-catalog-wrapper]");
    var gridBtn = document.querySelector("[data-view-grid]");
    var listBtn = document.querySelector("[data-view-list]");
    if (!wrapper) return;
    if (gridBtn) gridBtn.addEventListener("click", function () {
      wrapper.classList.remove("view-list");
      gridBtn.classList.add("bg-brand-purple", "text-white");
      if (listBtn) listBtn.classList.remove("bg-brand-purple", "text-white");
    });
    if (listBtn) listBtn.addEventListener("click", function () {
      wrapper.classList.add("view-list");
      listBtn.classList.add("bg-brand-purple", "text-white");
      if (gridBtn) gridBtn.classList.remove("bg-brand-purple", "text-white");
    });
  }

  function initFilterSidebar() {
    var overlay = document.querySelector("[data-filter-overlay]");
    if (!overlay) return;
    var openBtn = document.querySelector("[data-filter-open]");
    var closeBtn = document.querySelector("[data-filter-close]");
    function close() { overlay.classList.remove("open"); document.body.classList.remove("overflow-hidden"); }
    if (openBtn) openBtn.addEventListener("click", function () { overlay.classList.add("open"); document.body.classList.add("overflow-hidden"); });
    if (closeBtn) closeBtn.addEventListener("click", close);
    overlay.addEventListener("click", function (e) { if (e.target === overlay) close(); });
    var reset = document.querySelector("[data-filter-reset]");
    if (reset) reset.addEventListener("click", function () {
      overlay.querySelectorAll("input[type='checkbox']").forEach(function (cb) { cb.checked = false; });
      showToast("info", "Filtres réinitialisés");
    });
  }

  function initAddToCartButtons() {
    document.querySelectorAll("[data-add-to-cart]").forEach(function (btn) {
      btn.addEventListener("click", function (e) {
        e.preventDefault();
        var orig = btn.innerHTML;
        btn.disabled = true;
        btn.textContent = "Ajout…";
        setTimeout(function () {
          btn.textContent = "✓ Ajouté !";
          showToast("success", "Jeu ajouté au panier");
          setTimeout(function () { btn.disabled = false; btn.innerHTML = orig; }, 2000);
        }, 600);
      });
    });
  }

  function initImageZoom() {
    document.querySelectorAll(".image-zoom-container").forEach(function (c) {
      c.addEventListener("mousemove", function (e) {
        var r = c.getBoundingClientRect();
        c.style.setProperty("--zoom-x", ((e.clientX - r.left) / r.width) * 100 + "%");
        c.style.setProperty("--zoom-y", ((e.clientY - r.top) / r.height) * 100 + "%");
      });
    });
  }

  function initGallery() {
    var main = document.querySelector("[data-gallery-main]");
    if (!main) return;
    document.querySelectorAll("[data-gallery-thumb]").forEach(function (t) {
      t.addEventListener("click", function () {
        main.src = t.getAttribute("data-full") || t.querySelector("img").src;
        document.querySelectorAll("[data-gallery-thumb]").forEach(function (x) { x.classList.remove("ring-2", "ring-brand-purple"); });
        t.classList.add("ring-2", "ring-brand-purple");
      });
    });
  }

  function initTabs() {
    document.querySelectorAll("[data-tab-list]").forEach(function (tabList) {
      var tabs = tabList.querySelectorAll("[role='tab']");
      var root = tabList.closest("section") || tabList.parentElement;
      tabs.forEach(function (tab) {
        tab.addEventListener("click", function () {
          var target = tab.getAttribute("aria-controls");
          tabs.forEach(function (t) {
            t.setAttribute("aria-selected", "false");
            t.classList.remove("border-brand-purple", "text-white");
            t.classList.add("border-transparent", "text-muted");
          });
          tab.setAttribute("aria-selected", "true");
          tab.classList.add("border-brand-purple", "text-white");
          tab.classList.remove("border-transparent", "text-muted");
          (root || document).querySelectorAll("[role='tabpanel'], [data-tab-panel]").forEach(function (p) {
            p.classList.toggle("hidden", p.id !== target);
          });
        });
      });
    });
  }

  function initReviewStars() {
    document.querySelectorAll("[data-star-rating]").forEach(function (c) {
      var input = c.querySelector("input[type='hidden']");
      var stars = c.querySelectorAll("[data-star]");
      stars.forEach(function (star) {
        star.addEventListener("click", function () {
          var v = parseInt(star.getAttribute("data-star"), 10);
          if (input) input.value = v;
          stars.forEach(function (s, i) {
            s.classList.toggle("text-yellow-400", i < v);
            s.classList.toggle("text-border", i >= v);
          });
        });
      });
    });
  }

  function initAuthForms() {
    document.querySelectorAll("[data-password-toggle]").forEach(function (btn) {
      btn.addEventListener("click", function () {
        var input = document.getElementById(btn.getAttribute("aria-controls"));
        if (input) input.type = input.type === "password" ? "text" : "password";
      });
    });
    var loginForm = document.querySelector("[data-login-form]");
    if (loginForm) {
      loginForm.addEventListener("submit", function (e) {
        var email = loginForm.querySelector("[name='email']");
        var password = loginForm.querySelector("[name='password']");
        var err = document.querySelector("[data-login-error]");
        if (!email.value || !password.value) {
          e.preventDefault();
          if (err) err.classList.remove("hidden");
          showToast("error", "Email ou mot de passe incorrect");
          return;
        }
        e.preventDefault();
        var btn = loginForm.querySelector("[type='submit']");
        btn.disabled = true;
        btn.textContent = "Connexion…";
        setTimeout(function () { window.location.href = "account.html"; }, 1200);
      });
    }
    var regForm = document.querySelector("[data-register-form]");
    if (regForm) {
      var pwd = regForm.querySelector("[name='password']");
      var confirm = regForm.querySelector("[name='confirmPassword']");
      var strength = document.querySelector("[data-password-strength]");
      if (pwd && strength) {
        pwd.addEventListener("input", function () {
          var v = pwd.value, s = 0;
          if (v.length >= 8) s++; if (/[A-Z]/.test(v)) s++; if (/[0-9]/.test(v)) s++; if (/[^A-Za-z0-9]/.test(v)) s++;
          var L = ["", "Faible", "Moyen", "Fort", "Fort"];
          var C = ["", "strength-weak", "strength-medium", "strength-strong", "strength-strong"];
          strength.textContent = v ? L[s] : "";
          strength.className = "text-sm font-medium " + (C[s] || "");
        });
      }
      regForm.addEventListener("submit", function (e) {
        if (pwd && confirm && pwd.value !== confirm.value) { e.preventDefault(); showToast("error", "Les mots de passe ne correspondent pas"); }
      });
    }
    var forgot = document.querySelector("[data-forgot-form]");
    if (forgot) {
      forgot.addEventListener("submit", function (e) {
        e.preventDefault();
        forgot.classList.add("hidden");
        var ok = document.querySelector("[data-forgot-success]");
        if (ok) ok.classList.remove("hidden");
      });
    }
  }

  function initCart() {
    document.querySelectorAll("[data-qty-minus]").forEach(function (b) {
      b.addEventListener("click", function () {
        var i = b.parentElement.querySelector("[data-qty-input]");
        if (i) i.value = Math.max(1, parseInt(i.value, 10) - 1);
        updateLine(b.closest("[data-cart-item]"));
      });
    });
    document.querySelectorAll("[data-qty-plus]").forEach(function (b) {
      b.addEventListener("click", function () {
        var i = b.parentElement.querySelector("[data-qty-input]");
        if (i) i.value = Math.min(10, parseInt(i.value, 10) + 1);
        updateLine(b.closest("[data-cart-item]"));
      });
    });
    document.querySelectorAll("[data-remove-item]").forEach(function (b) {
      b.addEventListener("click", function () {
        var row = b.closest("[data-cart-item]");
        if (row) { row.classList.add("fade-out-remove"); setTimeout(function () { row.remove(); updateCartSummary(); }, 350); showToast("info", "Article retiré"); }
      });
    });
  }

  function updateLine(row) {
    if (!row) return;
    var p = parseFloat(row.getAttribute("data-unit-price"));
    var q = parseInt(row.querySelector("[data-qty-input]").value, 10);
    var t = row.querySelector("[data-line-total]");
    if (t && !isNaN(p)) t.textContent = (p * q).toFixed(2).replace(".", ",") + " €";
    updateCartSummary();
  }

  function updateCartSummary() {
    var sum = 0;
    document.querySelectorAll("[data-cart-item]").forEach(function (r) {
      sum += parseFloat(r.getAttribute("data-unit-price")) * parseInt(r.querySelector("[data-qty-input]").value, 10);
    });
    var s = document.querySelector("[data-cart-subtotal]");
    var t = document.querySelector("[data-cart-total]");
    if (s) s.textContent = sum.toFixed(2).replace(".", ",") + " €";
    if (t) t.textContent = Math.max(0, sum - 5).toFixed(2).replace(".", ",") + " €";
  }

  function initPromoCode() {
    var form = document.querySelector("[data-promo-form]");
    if (!form) return;
    form.addEventListener("submit", function (e) {
      e.preventDefault();
      var input = form.querySelector("input");
      var fb = document.querySelector("[data-promo-feedback]");
      if (!fb) return;
      fb.classList.remove("hidden", "text-success", "text-error");
      if (input.value.toUpperCase() === "GAME10") { fb.textContent = "✓ -15% appliqué"; fb.classList.add("text-success"); }
      else { fb.textContent = "✕ Code invalide"; fb.classList.add("text-error"); }
    });
  }

  function initCheckout() {
    document.querySelectorAll("[name='paymentMethod']").forEach(function (r) {
      r.addEventListener("change", function () {
        document.querySelectorAll("[data-payment-panel]").forEach(function (p) { p.classList.add("hidden"); });
        var panel = document.querySelector("[data-payment-panel='" + r.value + "']");
        if (panel) panel.classList.remove("hidden");
      });
    });
    var card = document.querySelector("[data-card-number]");
    var icon = document.querySelector("[data-card-icon]");
    if (card && icon) {
      card.addEventListener("input", function () {
        var v = card.value.replace(/\D/g, "");
        card.value = v.replace(/(.{4})/g, "$1 ").trim().slice(0, 19);
        icon.textContent = v.startsWith("4") ? "Visa" : v.startsWith("5") ? "MasterCard" : "💳";
      });
    }
    var form = document.querySelector("[data-checkout-form]");
    if (form) {
      form.addEventListener("submit", function (e) {
        e.preventDefault();
        var btn = form.querySelector("[data-checkout-submit]");
        if (btn) {
          btn.disabled = true;
          var spinner = btn.querySelector("[data-checkout-spinner]");
          var label = btn.querySelector("[data-submit-label]");
          if (spinner) spinner.classList.remove("hidden");
          if (label) label.textContent = "Traitement…";
          else btn.textContent = "Traitement…";
        }
        setTimeout(function () {
          var steps = document.querySelector("[data-checkout-steps]");
          var confirm = document.querySelector("[data-checkout-confirm]");
          if (steps) steps.classList.add("hidden");
          form.classList.add("hidden");
          if (confirm) confirm.classList.remove("hidden");
        }, 2000);
      });
    }
  }

  function initAccountNav() {
    document.querySelectorAll("[data-account-nav]").forEach(function (link) {
      link.addEventListener("click", function (e) {
        var id = link.getAttribute("data-section");
        if (!id) return;
        e.preventDefault();
        document.querySelectorAll("[data-account-nav]").forEach(function (l) {
          l.classList.remove("bg-brand-purple/20", "text-brand-cyan-light");
          l.classList.add("text-muted");
        });
        link.classList.add("bg-brand-purple/20", "text-brand-cyan-light");
        link.classList.remove("text-muted");
        document.querySelectorAll("[data-account-section]").forEach(function (s) {
          s.classList.toggle("hidden", s.id !== id);
        });
      });
    });
  }

  function initKeyReveal() {
    document.querySelectorAll("[data-reveal-key]").forEach(function (btn) {
      btn.addEventListener("click", function () {
        var card = btn.closest("[data-game-key-card]");
        var key = card.querySelector("[data-key-value]");
        key.classList.toggle("hidden");
        btn.textContent = key.classList.contains("hidden") ? "Voir ma clé" : "Masquer";
      });
    });
    document.querySelectorAll("[data-copy-key]").forEach(function (btn) {
      btn.addEventListener("click", function () {
        var code = btn.closest("[data-game-key-card]").querySelector("[data-key-code]");
        if (code && navigator.clipboard) {
          navigator.clipboard.writeText(code.textContent.trim());
          btn.textContent = "Copié !";
          showToast("success", "Clé copiée");
          setTimeout(function () { btn.textContent = "Copier"; }, 2000);
        }
      });
    });
  }

  function initOrderModal() {
    var modal = document.querySelector("[data-order-modal]");
    if (!modal) return;
    document.querySelectorAll("[data-order-detail]").forEach(function (b) {
      b.addEventListener("click", function () { modal.classList.remove("hidden"); document.body.classList.add("overflow-hidden"); });
    });
    modal.querySelectorAll("[data-modal-close]").forEach(function (b) {
      b.addEventListener("click", function () { modal.classList.add("hidden"); document.body.classList.remove("overflow-hidden"); });
    });
  }

  function initAdminModals() {
    var gm = document.querySelector("[data-game-modal]");
    var dm = document.querySelector("[data-delete-modal]");
    var add = document.querySelector("[data-add-game]");
    if (add && gm) add.addEventListener("click", function () { gm.classList.remove("hidden"); });
    document.querySelectorAll("[data-edit-game]").forEach(function (b) { b.addEventListener("click", function () { gm && gm.classList.remove("hidden"); }); });
    document.querySelectorAll("[data-delete-game]").forEach(function (b) { b.addEventListener("click", function () { dm && dm.classList.remove("hidden"); }); });
    document.querySelectorAll("[data-modal-close]").forEach(function (b) {
      b.addEventListener("click", function () { document.querySelectorAll("[data-game-modal],[data-delete-modal]").forEach(function (m) { m.classList.add("hidden"); }); });
    });
  }

  function initMaintenanceCountdown() {
    var el = document.querySelector("[data-maintenance-timer]");
    if (!el) return;
    var end = Date.now() + 9900000;
    setInterval(function () {
      var d = Math.max(0, end - Date.now());
      el.textContent = Math.floor(d / 3600000) + "h " + Math.floor((d % 3600000) / 60000) + "m " + Math.floor((d % 60000) / 1000) + "s";
    }, 1000);
  }

  function initEmailVerification() {
    var state = new URLSearchParams(window.location.search).get("state") || "success";
    document.querySelectorAll("[data-verify-state]").forEach(function (el) {
      el.classList.toggle("hidden", el.getAttribute("data-verify-state") !== state);
    });
  }
})();
