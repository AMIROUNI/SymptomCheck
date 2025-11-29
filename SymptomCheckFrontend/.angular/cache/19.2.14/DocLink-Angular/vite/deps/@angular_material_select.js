import {
  MAT_SELECT_CONFIG,
  MAT_SELECT_SCROLL_STRATEGY,
  MAT_SELECT_SCROLL_STRATEGY_PROVIDER,
  MAT_SELECT_SCROLL_STRATEGY_PROVIDER_FACTORY,
  MAT_SELECT_TRIGGER,
  MatOptgroup,
  MatOption,
  MatSelect,
  MatSelectChange,
  MatSelectModule,
  MatSelectTrigger
<<<<<<< HEAD
} from "./chunk-BR63PUBK.js";
import "./chunk-OJOE7IYI.js";
import "./chunk-VCO2DWYP.js";
=======
<<<<<<< HEAD
} from "./chunk-O24GJDGR.js";
import "./chunk-FEU2CRPU.js";
import "./chunk-OJOE7IYI.js";
import "./chunk-FVV64UU6.js";
=======
} from "./chunk-2MMCTQUO.js";
import "./chunk-SGSUD3PA.js";
import "./chunk-ZMVBMUTN.js";
import "./chunk-OJOE7IYI.js";
import "./chunk-DEXSUM5Q.js";
import "./chunk-WU3VM5QL.js";
>>>>>>> e1f17c155b7f75b68cd0a68dea4fd8860f59fd7b
>>>>>>> ae66bb5d18f919a951a8ad3f1c90930cd8ca11e6
import {
  MatError,
  MatFormField,
  MatHint,
  MatLabel,
  MatPrefix,
  MatSuffix
<<<<<<< HEAD
} from "./chunk-365BQ3HG.js";
import "./chunk-22BBKKVM.js";
import "./chunk-ZMVBMUTN.js";
import "./chunk-5K2BQUFO.js";
import "./chunk-BPXMWEGU.js";
import "./chunk-MYI4C4DP.js";
import "./chunk-3NGNURFM.js";
import "./chunk-I6IPDALT.js";
import "./chunk-I32RQRLC.js";
import "./chunk-2VJG4NBP.js";
=======
<<<<<<< HEAD
} from "./chunk-5ZLJCGEV.js";
import "./chunk-IPUNXTGC.js";
import "./chunk-ZMVBMUTN.js";
import "./chunk-CNXSYUBX.js";
import "./chunk-2VJG4NBP.js";
import "./chunk-3NGNURFM.js";
import "./chunk-3Q2D25A5.js";
import "./chunk-BPXMWEGU.js";
import "./chunk-MYI4C4DP.js";
import "./chunk-IBYU652R.js";
>>>>>>> ae66bb5d18f919a951a8ad3f1c90930cd8ca11e6
import "./chunk-42FJBLFI.js";
import "./chunk-IBYU652R.js";
import "./chunk-RGTUUXR4.js";
import "./chunk-2O4WY5GE.js";
import "./chunk-6DI5HFSH.js";
import "./chunk-T7A2XIJF.js";
import "./chunk-V4XTLCDT.js";
import "./chunk-QODMAC7P.js";
import "./chunk-D4PSHWS4.js";
=======
} from "./chunk-DI5PZSI2.js";
import "./chunk-NBSIT5KT.js";
import "./chunk-BPXMWEGU.js";
import "./chunk-MYI4C4DP.js";
import "./chunk-QKBBG5TN.js";
import "./chunk-GZWGXMEO.js";
import "./chunk-2VJG4NBP.js";
import "./chunk-IBYU652R.js";
import "./chunk-42FJBLFI.js";
import "./chunk-2O4WY5GE.js";
import "./chunk-7UYQUKYG.js";
import "./chunk-NJ6V4T54.js";
import "./chunk-T7A2XIJF.js";
import "./chunk-XEZNIQBN.js";
import "./chunk-V3NHMBNA.js";
import "./chunk-LW63EBUO.js";
>>>>>>> e1f17c155b7f75b68cd0a68dea4fd8860f59fd7b
import "./chunk-3W5R4R57.js";
import "./chunk-P6U2JBMQ.js";
import "./chunk-TXDUYLVM.js";

// node_modules/@angular/material/fesm2022/select.mjs
var matSelectAnimations = {
  // Represents
  // trigger('transformPanelWrap', [
  //   transition('* => void', query('@transformPanel', [animateChild()], {optional: true})),
  // ])
  /**
   * This animation ensures the select's overlay panel animation (transformPanel) is called when
   * closing the select.
   * This is needed due to https://github.com/angular/angular/issues/23302
   */
  transformPanelWrap: {
    type: 7,
    name: "transformPanelWrap",
    definitions: [{
      type: 1,
      expr: "* => void",
      animation: {
        type: 11,
        selector: "@transformPanel",
        animation: [{
          type: 9,
          options: null
        }],
        options: {
          optional: true
        }
      },
      options: null
    }],
    options: {}
  },
  // Represents
  // trigger('transformPanel', [
  //   state(
  //     'void',
  //     style({
  //       opacity: 0,
  //       transform: 'scale(1, 0.8)',
  //     }),
  //   ),
  //   transition(
  //     'void => showing',
  //     animate(
  //       '120ms cubic-bezier(0, 0, 0.2, 1)',
  //       style({
  //         opacity: 1,
  //         transform: 'scale(1, 1)',
  //       }),
  //     ),
  //   ),
  //   transition('* => void', animate('100ms linear', style({opacity: 0}))),
  // ])
  /** This animation transforms the select's overlay panel on and off the page. */
  transformPanel: {
    type: 7,
    name: "transformPanel",
    definitions: [{
      type: 0,
      name: "void",
      styles: {
        type: 6,
        styles: {
          opacity: 0,
          transform: "scale(1, 0.8)"
        },
        offset: null
      }
    }, {
      type: 1,
      expr: "void => showing",
      animation: {
        type: 4,
        styles: {
          type: 6,
          styles: {
            opacity: 1,
            transform: "scale(1, 1)"
          },
          offset: null
        },
        timings: "120ms cubic-bezier(0, 0, 0.2, 1)"
      },
      options: null
    }, {
      type: 1,
      expr: "* => void",
      animation: {
        type: 4,
        styles: {
          type: 6,
          styles: {
            opacity: 0
          },
          offset: null
        },
        timings: "100ms linear"
      },
      options: null
    }],
    options: {}
  }
};
export {
  MAT_SELECT_CONFIG,
  MAT_SELECT_SCROLL_STRATEGY,
  MAT_SELECT_SCROLL_STRATEGY_PROVIDER,
  MAT_SELECT_SCROLL_STRATEGY_PROVIDER_FACTORY,
  MAT_SELECT_TRIGGER,
  MatError,
  MatFormField,
  MatHint,
  MatLabel,
  MatOptgroup,
  MatOption,
  MatPrefix,
  MatSelect,
  MatSelectChange,
  MatSelectModule,
  MatSelectTrigger,
  MatSuffix,
  matSelectAnimations
};
//# sourceMappingURL=@angular_material_select.js.map
